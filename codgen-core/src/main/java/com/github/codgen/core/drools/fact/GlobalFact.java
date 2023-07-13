package com.github.codgen.core.drools.fact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalFact {
    /**
     * 模板文件路径
     */
    private Map<String, ?> globals;
    /**
     * 绑定的变量列表
     */
    private Map<String, ?> globalOptions;
}
