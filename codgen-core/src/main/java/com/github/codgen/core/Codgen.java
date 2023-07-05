package com.github.codgen.core;

import rebue.wheel.core.JdbcUtils;

import java.sql.SQLException;

public class Codgen {
    public static void gen(GenOptions options) throws SQLException {
        JdbcUtils.DbMeta dbMeta = JdbcUtils.getDbMeta(options.getJdbc().getConnect(), options.getJdbc().getTableName());
        System.out.printf("database meta: %s%n", dbMeta);
    }
}
