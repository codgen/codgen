package io.github.codgen.core.drools.fact;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.beetl.core.GroupTemplate;

import io.github.codgen.core.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateFact {
    /**
     * 路径GroupTemplate
     */
    private GroupTemplate        pathGroupTemplate;
    /**
     * 内容groupTemplate
     */
    private GroupTemplate        contentGroupTemplate;
    /**
     * 输入文件信息(其中的路径可用于判断激活哪个议程)
     */
    private FileInfo             inFileInfo;
    /**
     * 全局变量Map列表(用于绑定到模板中)
     */
    private Map<String, ?>       globalVariableMap;
    /**
     * 模板规则的选项Map列表
     */
    private Map<String, List<?>> ruleOptionsMap;
    /**
     * 模板信息列表(返回主程给后续流程使用)
     */
    @Builder.Default
    private List<TemplateInfo>   templates = new LinkedList<>();

}
