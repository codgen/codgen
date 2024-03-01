package com.github.codgen.core.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取模板的选项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateOptions {
    /**
     * groupTemplate的配置选项列表
     */
    @Builder.Default
    private Map<String, List<String>> groupTemplate = new HashMap<>();
    /**
     * 规则的配置选项列表
     */
    @Builder.Default
    private Map<String, List<?>>      rule          = new HashMap<>();
}
