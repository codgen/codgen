package com.github.codgen.core.drools.fact;

import com.github.codgen.core.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateFact {
    /**
     * groupTemplate列表
     */
    private Map<String, GroupTemplate> groupTemplates;
    /**
     * 模板文件信息(其中的路径可用于判断激活哪个议程)
     */
    private FileInfo                   templateFileInfo;
    /**
     * 路径模板(返回给主程使用)
     */
    private Template                   pathTemplate;
    /**
     * 内容模板(返回给主程使用)
     */
    private Template                   contentTemplate;
}
