package com.github.codgen.core;

import lombok.Data;
import rebue.wheel.core.JdbcUtils;

@Data
public class GenOptions {
    private JdbcOptions jdbc;

    @Data
    public static class JdbcOptions {
        /**
         * jdbc的连接参数
         */
        private JdbcUtils.ConnectParam connect;
        /**
         * 过滤表名的正则表达式
         */
        private String                 tableName;

    }
}
