package com.github.codgen.core.drools.fact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariableFact {
    /**
     * 变量选项列表(添加变量可能会使用一些选项，例如数据库连接参数)
     */
    private Map<String, ?> variableOptions;
    /**
     * 要忽略的变量选项列表(前置规则中添加本规则所负责变量选项的位置，避免后置规则重复读取该变量选项)
     */
    private List<String>   ignoreVariableOptions;
    /**
     * 变量Map列表(添加变量后返回给主程使用)
     */
    private Map<String, ?> variableMap;
}
