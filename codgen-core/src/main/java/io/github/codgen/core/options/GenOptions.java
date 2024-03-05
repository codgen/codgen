package io.github.codgen.core.options;

import lombok.Data;

import java.util.Map;
import java.util.Properties;

@Data
public class GenOptions {
    /**
     * groupTemplate的配置选项
     */
    private Map<String, Properties>     groupTemplate;
    /**
     * template的配置选项
     */
    private TemplateOptions             template;
    /**
     * 全局变量的配置选项
     */
    private Map<String, Map<String, ?>> globalVariable;
}
