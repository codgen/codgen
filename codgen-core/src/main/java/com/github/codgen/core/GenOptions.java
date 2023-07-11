package com.github.codgen.core;

import lombok.Data;
import rebue.wheel.core.JdbcUtils;

import java.util.Map;

@Data
public class GenOptions {
    /**
     * jdbc的配置选项
     */
    private Map<String, JdbcOptions> jdbc;
    /**
     * 绑定的变量
     */
    private Map<String, Map<String, ?>> binding;
    /**
     * groupTemplate的配置选项
     */
    private Map<String, Map<String, ?>> groupTemplate;

    @Data
    public static class JdbcOptions {
        /**
         * jdbc的连接参数
         */
        private JdbcUtils.ConnectParam connect;
        /**
         * 过滤表名的正则表达式
         */
        private String tableName;
    }

}
