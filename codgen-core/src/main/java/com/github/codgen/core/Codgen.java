package com.github.codgen.core;

import com.github.codgen.core.drools.fact.BindingsFact;
import com.github.codgen.core.drools.fact.RenderFact;
import com.github.codgen.core.drools.fact.TemplateFact;
import com.github.codgen.core.drools.fact.VariableFact;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.kie.api.runtime.KieContainer;
import rebue.wheel.core.MapUtils;
import rebue.wheel.core.drools.DroolsUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Codgen {
    /**
     * 变量的议程分组名称
     */
    private static final String VARIABLE_AGENDA_GROUP_NAME = "variable";
    /**
     * 模板的议程分组名称
     */
    private static final String TEMPLATE_AGENDA_GROUP_NAME = "template";
    /**
     * 绑定的议程分组名称
     */
    private static final String BINDINGS_AGENDA_GROUP_NAME = "bindings";
    /**
     * 渲染的议程分组名称
     */
    private static final String RENDER_AGENDA_GROUP_NAME   = "render";

    /**
     * 生成输出文件信息列表
     *
     * @param inFileInfos 输入文件信息列表
     * @param genOptions  生成的配置选项
     * @param rules       规则文件内容Map列表
     * @return 输出文件信息列表
     */
    public static List<FileInfo> gen(List<FileInfo> inFileInfos, GenOptions genOptions, Map<String, String> rules) throws IOException {
        // 创建GroupTemplates的Map列表
        Map<String, GroupTemplate> groupTemplates = createGroupTemplates(genOptions.getGroupTemplate());

        // 创建drools容器
        KieContainer kieContainer = DroolsUtils.newKieContainer(null, rules);

        // 触发variable规则，读取配置文件中的变量
        Map<String, Map<String, ?>> variableMap = new HashMap<>();
        DroolsUtils.fireRules(kieContainer, null, VARIABLE_AGENDA_GROUP_NAME, VariableFact.builder()
                .variableOptions(genOptions.getVariable())
                .ignoreVariableOptions(new LinkedList<>())
                .variableMap(variableMap)
                .build());

        // 输出文件信息列表
        List<FileInfo> outFileInfos = new LinkedList<>();
        // 遍历输入文件信息列表
        for (FileInfo inFileInfo : inFileInfos) {
            // 触发template规则，获取路径模板和内容模板
            TemplateFact templateFact = TemplateFact.builder()
                    .groupTemplates(groupTemplates)
                    .templateFileInfo(inFileInfo)
                    .build();
            DroolsUtils.fireRules(kieContainer, null, TEMPLATE_AGENDA_GROUP_NAME, templateFact);
            // 从fact返回中获取模板
            Template pathTemplate    = templateFact.getPathTemplate();
            Template contentTemplate = templateFact.getContentTemplate();
            // 触发bindings规则，绑定变量到模板中
            DroolsUtils.fireRules(kieContainer, null, BINDINGS_AGENDA_GROUP_NAME, BindingsFact.builder()
                    .templateFileInfo(inFileInfo)
                    .variableMap(variableMap)
                    .bindingsOptionsMap(genOptions.getBindings())
                    .pathTemplate(pathTemplate)
                    .contentTemplate(contentTemplate)
                    .build());
            // 触发render规则，根据模板渲染输出文件
            DroolsUtils.fireRules(kieContainer, null, RENDER_AGENDA_GROUP_NAME, RenderFact.builder()
                    .templateFileInfo(inFileInfo)
                    .pathTemplate(pathTemplate)
                    .contentTemplate(contentTemplate)
                    .outFileInfos(outFileInfos)
                    .build());
        }
        System.out.printf("outFileInfos: %s%n", outFileInfos);
        return outFileInfos;
    }

    /**
     * 创建GroupTemplates的Map列表
     *
     * @param groupTemplatesOptions 创建GroupTemplate的Map列表的选项
     * @return GroupTemplate的Map列表
     */
    private static Map<String, GroupTemplate> createGroupTemplates(Map<String, Map<String, ?>> groupTemplatesOptions) throws IOException {
        Map<String, GroupTemplate>   groupTemplates = new HashMap<>();
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        if (groupTemplatesOptions == null || groupTemplatesOptions.isEmpty()) {
            groupTemplates.put("default", new GroupTemplate(resourceLoader, new Configuration()));
        } else {
            for (Map.Entry<String, Map<String, ?>> groupTemplateConfig : groupTemplatesOptions.entrySet()) {
                groupTemplates.put(groupTemplateConfig.getKey(), new GroupTemplate(resourceLoader,
                        new Configuration(MapUtils.map2Props(groupTemplateConfig.getValue()))));
            }
        }
        return groupTemplates;
    }

}
