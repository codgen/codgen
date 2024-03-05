package io.github.codgen.core.drools.fact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.core.Template;

/**
 * 模板信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateInfo {
    /**
     * 路径模板
     */
    private Template pathTemplate;
    /**
     * 内容模板
     */
    private Template contentTemplate;
}
