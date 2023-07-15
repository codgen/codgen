package com.github.codgen.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GenOptions {
    /**
     * groupTemplate的配置选项
     */
    private Map<String, Map<String, ?>> groupTemplate;
    /**
     * 变量的配置选项
     */
    private Map<String, ?>              variable;
    /**
     * 绑定的配置选项
     */
    private Map<String, List<String>>   bindings;

}
