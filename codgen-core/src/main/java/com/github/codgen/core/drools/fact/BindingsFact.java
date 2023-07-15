package com.github.codgen.core.drools.fact;

import com.github.codgen.core.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.core.Template;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BindingsFact {
    /**
     * 模板文件信息(其中的路径可用于判断激活哪个议程)
     */
    private FileInfo                    templateFileInfo;
    /**
     * 变量Map列表(添加绑定可能会使用一些变量，例如数据库信息)
     */
    private Map<String, Map<String, ?>> variableMap;
    /**
     * 绑定选项Map列表(添加绑定可能会使用一些选项，例如项目名称等参数)
     */
    private Map<String, List<String>>   bindingsOptionsMap;
    /**
     * 路径模板(返回给主程使用)
     */
    private Template                    pathTemplate;
    /**
     * 内容模板(返回给主程使用)
     */
    private Template                    contentTemplate;

}
