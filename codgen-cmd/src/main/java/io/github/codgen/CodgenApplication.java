package io.github.codgen;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import io.github.codgen.co.TagsCo;
import io.github.codgen.core.Codgen;
import io.github.codgen.core.FileInfo;
import io.github.codgen.core.options.GenOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.PomUtils;
import rebue.wheel.core.PrintUtils;
import rebue.wheel.core.drools.DroolsUtils;
import rebue.wheel.core.file.FileSearcher;
import rebue.wheel.core.file.FileUtils;
import rebue.wheel.core.source.MergeJavaFileUtils;

@Slf4j
public class CodgenApplication {
    /**
     * Codgen的配置文件名称
     */
    private static final String CONFIG_FILE_NAME     = "codgen.yml";
    /**
     * drools的目录的路径
     */
    private static final String DROOLS_RULE_DIR_NAME = ".drl";

    public static void main(String[] args) throws IOException {
        // 获取pom中设置的属性
        PomUtils.PomProps pomProps   = PomUtils.getPomProps(Path.of(File.separator, "conf", "pom.properties").toString(), CodgenApplication.class);

        // 读取与校验参数
        CmdOptions        cmdOptions = parseCmdOptions(args, pomProps);
        if (cmdOptions == null)
            return;

        // 打印横幅
        printBanner(pomProps, cmdOptions.inPath.toString(), cmdOptions.outPath.toString());
        System.out.printf("generate code from %s to %s%n", cmdOptions.inPath, cmdOptions.outPath);

        // 解析配置文件
        GenOptions genOptions = parseConfigFile(cmdOptions.inPath);
        if (genOptions == null)
            return;

        // 读取规则文件到Map列表中
        Map<String, String> ruleFiles   = DroolsUtils.readRuleFiles(cmdOptions.inPath.resolve(DROOLS_RULE_DIR_NAME));

        // 获取输入文件信息列表
        List<FileInfo>      inFileInfos = listInFiles(cmdOptions.inPath);

        // 生成输出文件
        genOutFiles(cmdOptions, genOptions, ruleFiles, inFileInfos);
    }

    /**
     * 解析命令的参数选项
     *
     * @param args     命令的参数
     * @param pomProps pom中设置的属性
     * @return 命令选项
     */
    private static CmdOptions parseCmdOptions(String[] args, PomUtils.PomProps pomProps) {
        String in  = null;
        String out = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
            case "-i", "--in" -> in = args[++i];
            case "-o", "--out" -> out = args[++i];
            case "-v", "--version" -> {
                printVersion(pomProps);
                return null;
            }
            case "-h", "--help" -> {
                printHelp();
                return null;
            }
            default -> {
                System.out.printf("Unrecognized option: %s%n", arg);
                printHelp();
                return null;
            }
            }
        }
        if (in == null) {
            PrintUtils.printlnError("error: missing -i option");
            System.out.println();
            printHelp();
            return null;
        }
        if (out == null) {
            PrintUtils.printlnError("error: missing -o option");
            System.out.println();
            printHelp();
            return null;
        }

        Path workDirPath = Path.of("");
        Path inPath      = Path.of(in);
        Path outPath     = Path.of(out);
        // 如果是相对路径就算出绝对路径
        if (!inPath.isAbsolute()) {
            inPath = workDirPath.resolve(inPath);
        }
        if (!outPath.isAbsolute()) {
            outPath = workDirPath.resolve(outPath);
        }
        if (validDirPath(inPath))
            return null;
        // 如果输出目录不存在，则创建它
        if (!outPath.toFile().exists()) {
            outPath.toFile().mkdirs();
        }

        return CmdOptions.builder()
                .inPath(inPath)
                .outPath(outPath)
                .build();
    }

    /**
     * 获取输入文件信息列表(忽略codgen.yml及Drools规则文件目录)
     *
     * @param inPath 输入目录的路径
     * @return 输入文件信息列表
     */
    private static List<FileInfo> listInFiles(Path inPath) {
        System.out.printf("search %s's files:%n", inPath);
        List<FileInfo>   fileInfos   = new LinkedList<>();
        List<IgnorePath> ignorePaths = new LinkedList<>();
        ignorePaths.add(IgnorePath.builder()
                .isDir(false)
                .path(inPath.resolve(CONFIG_FILE_NAME))
                .build());
        ignorePaths.add(IgnorePath.builder()
                .isDir(true)
                .path(inPath.resolve(DROOLS_RULE_DIR_NAME))
                .build());
        FileSearcher.searchFiles(inPath.toFile(), file -> {
            try {
                Path filePath = Path.of(file.getCanonicalPath());
                // 排除要忽略的文件和目录
                for (IgnorePath ignorePath : ignorePaths) {
                    if (Files.isDirectory(filePath) == ignorePath.isDir && Files.isSameFile(filePath, ignorePath.path)) {
                        return false;
                    }
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, file -> {
            try {
                System.out.printf("  - %s%n", file.getCanonicalPath());
                fileInfos.add(FileInfo.builder()
                        .path(inPath.relativize(file.toPath()).toString().replace('\\', '/'))
                        .content(FileUtils.readToString(file.getCanonicalPath()))
                        .build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return fileInfos;
    }

    /**
     * 解析配置文件
     *
     * @param inPath 来源目录的路径
     * @return 配置详情的对象，不存在则返回null
     */
    private static GenOptions parseConfigFile(Path inPath) throws IOException {
        Path configFilePath = inPath.resolve(CONFIG_FILE_NAME);
        if (!Files.exists(configFilePath)) {
            PrintUtils.printError("error: %s doesn't exist%n", configFilePath);
            return null;
        }
        System.out.printf("parse config file for generated code: %s%n", configFilePath);
        Yaml yaml = new Yaml();
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath.toFile()))) {
            return yaml.loadAs(reader, GenOptions.class);
        }
    }

    /**
     * 打印横幅
     *
     * @param pomProps pom.xml文件的属性
     * @param inDir    输入目录
     * @param outDir   输出目录
     */
    private static void printBanner(PomUtils.PomProps pomProps, String inDir, String outDir) {
        List<String> lines = new LinkedList<>();
        lines.add(PrintUtils.ConsoleColors.YELLOW_BOLD_BRIGHT);
        lines.add("""
                      ______          __
                     / ____/___  ____/ /___ ____  ____
                    / /   / __ \\/ __  / __ `/ _ \\/ __ \\
                   / /___/ /_/ / /_/ / /_/ /  __/ / / /
                   \\____/\\____/\\__,_/\\__, /\\___/_/ /_/
                                    /____/
                """);
        lines.add(":: codgen :: v%s :: nnzbz :: %s".formatted(pomProps.getVersion(), pomProps.getTimestamp()));
        lines.add(PrintUtils.ConsoleColors.RESET);
        lines.add("cmdOptions:");
        lines.add("    source      directory: %s".formatted(inDir));
        lines.add("    destination directory: %s".formatted(outDir));
        lines.add("(( ");
        lines.add("\\\\``. ");
        lines.add("\\_`.``-. ");
        lines.add("( `.`.` `._ ");
        lines.add(" `._`-.    `._ ");
        lines.add("   \\`--.   ,' `. ");
        lines.add("    `--._  `.  .`. ");
        lines.add("     `--.--- `. ` `. ");
        lines.add("         `.--  `;  .`._ ");
        lines.add("           :-   :   ;. `.__,.,__ __ ");
        lines.add("            `\\  :       ,-(     ';o`>. ");
        lines.add("              `-.`:   ,'   `._ .:  (,-`, ");
        lines.add("                 \\    ;      ;.  ,: ");
        lines.add("             ,\"`-._>-:        ;,'  `---.,---. ");
        lines.add("             `>'\"  \"-`       ,'   \"\":::::\".. `-.");
        lines.add("              `;\"'_,  (\\`\\ _ `:::::::::::'\"     `---. ");
        lines.add("               `-(_,' -'),)\\`.       _      .::::\"'  `----._,-\"\") ");
        lines.add("                   \\_,': `.-' `-----' `--;-.   `.   ``.`--.____/ ");
        lines.add("                     `-^--'                \\(-.  `.``-.`-=:-.__) ");
        lines.add("                                            `  `.`.`._`.-._`--.) ");
        lines.add("                                                 `-^---^--.`-- ");
        lines.add("");
        lines.add(StringUtils.repeat('-', 100));
        PrintUtils.printBanner(lines);
    }

    /**
     * 校验路径是否存在且是目录
     *
     * @param dirPath 目录路径
     * @return 路径是否存在且是目录
     */
    private static boolean validDirPath(Path dirPath) {
        if (!Files.exists(dirPath)) {
            PrintUtils.printError("error: %s doesn't exist%n", dirPath.toString());
            return true;
        }
        if (!Files.isDirectory(dirPath)) {
            PrintUtils.printError("error: %s isn't a directory%n", dirPath.toString());
            return true;
        }
        return false;
    }

    /**
     * 打印版本信息
     *
     * @param pomProps pom.xml文件的属性
     */
    private static void printVersion(PomUtils.PomProps pomProps) {
        System.out.printf("codgen.version: v%s%n", pomProps.getVersion());
        System.out.printf("java.version  : v%s%n", System.getProperty("java.version"));
    }

    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("""

                Usage: codgen-cmd [cmdOptions]

                where cmdOptions include:
                    -i, --in        source path
                    -o, --out       destination path
                    -v, --version   print product version to the error stream and exit
                    -h, --help      print this help message to the output stream
                """);
    }

    /**
     * 生成输出文件
     *
     * @param cmdOptions  命令行传入参数选项
     * @param genOptions  生成参数选项
     * @param ruleFiles   drools规则文件的Map列表
     * @param inFileInfos 输入文件信息列表
     */
    private static void genOutFiles(CmdOptions cmdOptions, GenOptions genOptions, Map<String, String> ruleFiles, List<FileInfo> inFileInfos) throws IOException {
        // 生成输出文件信息列表
        List<FileInfo> outFileInfos = Codgen.gen(inFileInfos, genOptions, ruleFiles);
        log.info("begin out files:");
        // 生成输出文件
        for (FileInfo outFileInfo : outFileInfos) {
            // 输出文件
            File outFile = cmdOptions.outPath.resolve(outFileInfo.getPath()).toFile();
            // 输出目录
            File outDir  = outFile.getParentFile();
            log.info(outFile.getAbsolutePath());
            // 如果目录不存在则创建
            outDir.mkdirs();

            log.info(outFileInfo.getContent());

            // 如果是java文件，且已经存在，合并代码
            if (outFile.getAbsolutePath().endsWith(".java") && outFile.exists()) {
                outFileInfo.setContent(MergeJavaFileUtils.merge(outFileInfo.getContent(), outFile,
                        TagsCo.generatedTags, TagsCo.removedMemberTags, TagsCo.dontOverWriteFileTags,
                        TagsCo.dontOverWriteAnnotationTags, TagsCo.dontOverWriteExtendsTags,
                        TagsCo.dontOverWriteImplementsTags));
            }

            // 写入文件内容
            try (BufferedWriter out = new BufferedWriter(new FileWriter(outFile))) {
                out.write(outFileInfo.getContent());
            }
        }
        log.info("end out files.");
    }

    /**
     * 命令行输入的参数选项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class CmdOptions {
        /**
         * 输入目录路径
         */
        private Path inPath;
        /**
         * 输出目录路径
         */
        private Path outPath;
    }

    /**
     * 要忽略的路径信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class IgnorePath {
        /**
         * 是否目录(不是目录则是文件)
         */
        private Boolean isDir;
        /**
         * 路径
         */
        private Path    path;
    }
}