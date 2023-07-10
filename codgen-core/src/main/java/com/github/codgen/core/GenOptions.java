package com.github.codgen.core;

import lombok.Data;
import rebue.wheel.core.JdbcUtils;

import java.util.Map;

@Data
public class GenOptions {
    /**
     * jdbc选项
     */
    private JdbcOptions  jdbc;
    /**
     * beetl选项
     */
    private Map<String, Object> beetl;

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
