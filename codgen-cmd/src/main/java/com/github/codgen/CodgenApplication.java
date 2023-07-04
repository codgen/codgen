package com.github.codgen;

import com.github.codgen.config.ApplicationConfig;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import rebue.wheel.core.PomUtils;
import rebue.wheel.core.PrintUtils;
import rebue.wheel.core.file.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CodgenApplication {
    public static void main(String[] args) throws IOException {
        PomUtils.PomProps pomProps = PomUtils.getPomProps("/conf/pom.properties", CodgenApplication.class);

        String in  = null;
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

        File curDir = new File(".");
        if (!FileUtils.isAbsPath(in)) {
            in = curDir.getCanonicalPath() + File.separator + in;
        }
        if (!FileUtils.isAbsPath(out)) {
            out = curDir.getCanonicalPath() + File.separator + out;
        }
        if (validDir(in)) return;
        if (validDir(out)) return;

        printBanner(pomProps, in, out);

        System.out.printf("generate code from %s to %s%n", in, out);
        String configFilePath = in + File.separator + "codgen.yml";
        File   configFile     = new File(configFilePath);
        if (!configFile.exists()) {
            PrintUtils.printError("error: %s doesn't exist%n", configFile);
            return;
        }
        System.out.printf("parse config file for generated code: %s%n", configFilePath);
        Yaml              yaml = new Yaml();
        ApplicationConfig applicationConfig;
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            applicationConfig = yaml.loadAs(reader, ApplicationConfig.class);
        }
        System.out.println(applicationConfig);
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

    private static boolean validDir(String dir) {
        File inDir = new File(dir);
        if (!inDir.exists()) {
            PrintUtils.printError("error: %s doesn't exist%n", dir);
            return true;
        }
        if (inDir.isFile()) {
            PrintUtils.printError("error: %s isn't a directory%n", dir);
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