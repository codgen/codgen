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
public class BindingsFact {
    /**
     * 模板文件路径
     */
    private String filePath;
    /**
     * 绑定的变量列表
     */
    private Map<String, ?> bindings;
}
