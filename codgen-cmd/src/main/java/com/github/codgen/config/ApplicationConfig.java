package com.github.codgen.config;

import lombok.Data;
import rebue.wheel.core.JdbcUtils;

@Data
public class ApplicationConfig {
    private JdbcUtils.ConnectParam jdbc;
}
