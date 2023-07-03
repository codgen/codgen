package com.github.codgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rebue.wheel.core.PrintUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CodgenApplicationTests {
    @Test
    void testMain() throws IOException {
        CodgenApplication.main(new String[]{"-i", "target/test-classes/in", "-o", "target/test-classes/out"});
    }

    @Test
    void testCmd() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{
                "target/codgen-cmd", "-i", "target/test-classes/in", "-o", "target/test-classes/out"
        });
        // 获取正常输出流
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // 获取错误输出流
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                PrintUtils.printError("%s%n", line);
            }
        }

        int status = process.waitFor();
        Assertions.assertEquals(0, status);
    }
}
