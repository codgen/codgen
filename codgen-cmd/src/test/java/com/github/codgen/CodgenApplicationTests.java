package com.github.codgen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rebue.wheel.core.CmdUtils;

import java.io.IOException;
import java.sql.SQLException;

public class CodgenApplicationTests {
    @Test
    void testMain() throws IOException, SQLException {
        CodgenApplication.main(new String[]{"-i", "target/test-classes/in", "-o", "target/test-classes/out"});
    }

    @Test
    void testCmd() throws IOException, InterruptedException {
        int status = CmdUtils.exec(new String[]{
                "target/codgen-cmd", "-i", "target/test-classes/in", "-o", "target/test-classes/out"
        });
        Assertions.assertEquals(0, status);
    }
}
