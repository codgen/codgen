package io.github.codgen;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import rebue.wheel.core.CmdUtils;

public class CodgenApplicationTests {
    @Test
    void testMain() throws IOException {
        CodgenApplication.main(new String[] { "-i", "target/test-classes/in", "-o", "target/test-classes/out" });
    }

    @Test
    void testCmd() throws IOException, InterruptedException {
        int status = CmdUtils.exec(new String[] {
                "java", "-jar", "target/codgen-cmd.jar", "-i", "target/test-classes/in", "-o", "target/test-classes/out"
        });
        Assertions.assertEquals(0, status);
    }

    @Test
    void testNative() throws IOException, InterruptedException {
        int status = CmdUtils.exec(new String[] {
                "target/codgen-cmd", "-i", "target/test-classes/in", "-o", "target/test-classes/out"
        });
        Assertions.assertEquals(0, status);
    }
}
