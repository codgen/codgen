package com.github.codgen.core;

import lombok.Data;

import java.util.Map;

@Data
public class GenOptions {
    /**
     * 绑定的变量
     */
    private Map<String, Map<String, ?>> bindings;
    /**
     * groupTemplate的配置选项
     */
    private Map<String, Map<String, ?>> groupTemplate;
    /**
     * 全局变量的配置选项
     */
    private Map<String, ?>              global;
//    /**
//     * jdbc的配置选项
//     */
//    private Map<String, JdbcOptions>    jdbc;
//
//    @Data
//    public static class JdbcOptions {
//        /**
//         * jdbc的连接参数
//         */
//        private JdbcUtils.ConnectParam connect;
//        /**
//         * 过滤表名的正则表达式
//         */
//        private String                 tableName;
//    }

}
