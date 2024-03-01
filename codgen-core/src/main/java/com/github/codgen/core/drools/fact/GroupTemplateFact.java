package com.github.codgen.core.drools.fact;

import com.github.codgen.core.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.core.GroupTemplate;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTemplateFact {
    /**
     * groupTemplate列表
     */
    private Map<String, GroupTemplate> groupTemplates;
    /**
     * 输入文件信息(其中的路径可用于判断激活哪个议程)
     */
    private FileInfo                   inFileInfo;
    /**
     * groupTemplate的选项Map列表
     */
    private Map<String, List<String>>  groupTemplateOptionsMap;
    /**
     * groupTemplate(返回主程给后续流程使用)
     */
    private GroupTemplate              groupTemplate;

}

