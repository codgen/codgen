package io.github.codgen.core.drools.fact;

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
     * 全局变量Map列表(用于绑定到模板中)
     */
    private Map<String, ?> globalVariableMap;
    /**
     * 模板信息
     */
    private TemplateInfo   templateInfo;

}
