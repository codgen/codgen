package com.github.codgen.core.drools.fact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalVariableFact {
    /**
     * 全局变量选项Map列表(配置的内容)
     */
    private Map<String, ?> variableOptions;
    /**
     * 全局变量Map列表(通过变量选项Map列表生成，然后返回给主程，后续用于绑定到模板中)
     */
    @Builder.Default
    private Map<String, ?> variableMap = new LinkedHashMap<>();
}
