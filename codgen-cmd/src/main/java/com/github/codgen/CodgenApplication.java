package com.github.codgen;

import com.github.codgen.core.Codgen;
import com.github.codgen.core.GenOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import rebue.wheel.core.PomUtils;
import rebue.wheel.core.PrintUtils;
import rebue.wheel.core.file.FileSearcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CodgenApplication {
    private static final String CONFIG_FILE_NAME = "codgen.yml";

    public static void main(String[] args) throws IOException, SQLException {
        PomUtils.PomProps pomProps = PomUtils.getPomProps("/conf/pom.properties", CodgenApplication.class);

        String in = null;
        String out = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-i", "--in" -> in = args[++i];
                case "-o", "--out" -> out = args[++i];
                case "-v", "--version" -> {
                    printVersion(pomProps);
                    return;
                }
                case "-h", "--help" -> {
                    printHelp();
                    return;
                }
                default -> {
                    System.out.printf("Unrecognized option: %s%n", arg);
                    printHelp();
                    return;
                }
            }
        }
        if (in == null) {
            PrintUtils.printlnError("error: missing -i option");
            System.out.println();
            printHelp();
            return;
        }
        if (out == null) {
            PrintUtils.printlnError("error: missing -o option");
            System.out.println();
            printHelp();
            return;
        }

        Path workDirPath = Path.of("");
        Path inPath = Path.of(in);
        Path outPath = Path.of(out);
        // 如果是相对路径就算出绝对路径
        if (!inPath.isAbsolute()) {
            inPath = workDirPath.resolve(inPath);
        }
        if (!outPath.isAbsolute()) {
            outPath = workDirPath.resolve(outPath);
        }
        if (validDirPath(inPath)) return;
        if (validDirPath(outPath)) return;


        // 打印横幅
        printBanner(pomProps, inPath.toString(), outPath.toString());

        System.out.printf("generate code from %s to %s%n", inPath, outPath);

        GenOptions options = parseConfigFile(inPath);
        if (options == null) return;

        System.out.printf("search %s's files%n", inPath);
        Path inPathTemp = inPath;
        List<IgnorePath> ignorePaths = new LinkedList<>();
        ignorePaths.add(IgnorePath.builder()
                .isDir(false)
                .path(inPathTemp.resolve(CONFIG_FILE_NAME))
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
                System.out.println(file.getCanonicalPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Codgen.gen(options);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class IgnorePath {
        private Boolean isDir;
        private Path path;
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

    private static void printBanner(PomUtils.PomProps pomProps, String in, String out) {
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
        lines.add(":: codgen :: v%s :: nnzbz :: %s".formatted(pomProps.getVersion(), pomProps.getDatetime()));
        lines.add(PrintUtils.ConsoleColors.RESET);
        lines.add("options:");
        lines.add("    source      directory: %s".formatted(in));
        lines.add("    destination directory: %s".formatted(out));
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

    private static void printVersion(PomUtils.PomProps pomProps) {
        System.out.printf("codgen.version: v%s%n", pomProps.getVersion());
        System.out.printf("java.version  : v%s%n", System.getProperty("java.version"));
    }

    private static void printHelp() {
        System.out.println("""
                                
                Usage: codgen-cmd [options]
                                
                where options include:
                    -i, --in        source path
                    -o, --out       destination path
                    -v, --version   print product version to the error stream and exit
                    -h, --help      print this help message to the output stream
                """);
    }


}