package com.github.codgen.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import rebue.wheel.core.JdbcUtils;
import rebue.wheel.serialization.jackson.JacksonUtils;

import java.sql.SQLException;

public class Codgen {
    public static void gen(GenOptions options) throws SQLException, JsonProcessingException {
        JdbcUtils.DbMeta dbMeta = JdbcUtils.getDbMeta(options.getJdbc().getConnect(), options.getJdbc().getTableName());
        System.out.printf("database meta: %s%n", JacksonUtils.serializeWithPretty(dbMeta));
    }
}
