package io.github.codgen.core;

import java.io.IOException;
import java.util.*;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.kie.api.runtime.KieContainer;

import io.github.codgen.core.drools.fact.*;
import io.github.codgen.core.options.GenOptions;
import io.github.codgen.core.options.TemplateOptions;
import rebue.wheel.core.drools.DroolsUtils;

public class Codgen {
    /**
     * 全局变量的议程分组名称
     */
    private static final String GLOBAL_VARIABLE_AGENDA_GROUP_NAME = "global-variable";
    /**
     * groupTemplate的议程分组名称
     */
    private static final String GROUP_TEMPLATE_AGENDA_GROUP_NAME  = "group-template";
    /**
     * 模板的议程分组名称
     */
    private static final String TEMPLATE_AGENDA_GROUP_NAME        = "template";
    /**
     * 绑定的议程分组名称
     */
    private static final String BINDINGS_AGENDA_GROUP_NAME        = "bindings";
    /**
     * 渲染的议程分组名称
     */
    private static final String RENDER_AGENDA_GROUP_NAME          = "render";

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
        Map<String, GroupTemplate> groupTemplates     = createGroupTemplates(genOptions.getGroupTemplate());

        // 获取模板选项的Map列表
        TemplateOptions            templateOptions    = genOptions.getTemplate();

        // 创建drools容器
        KieContainer               kieContainer       = DroolsUtils.newKieContainer(null, rules);

        // 触发global-variable规则，读取配置文件中的变量选项，生成要绑定的全局变量
        GlobalVariableFact         globalVariableFact = GlobalVariableFact.builder()
                .variableOptions(genOptions.getGlobalVariable())
                .build();
        DroolsUtils.fireRules(kieContainer, null, GLOBAL_VARIABLE_AGENDA_GROUP_NAME, globalVariableFact);

        // 输出文件信息列表
        List<FileInfo> outFileInfos = new LinkedList<>();
        // 遍历输入文件信息列表
        for (FileInfo inFileInfo : inFileInfos) {
            // 触发groupTemplate规则，获取groupTemplate
            GroupTemplateFact groupTemplateFact = GroupTemplateFact.builder()
                    .groupTemplates(groupTemplates)
                    .inFileInfo(inFileInfo)
                    .groupTemplateOptionsMap(templateOptions.getGroupTemplate())
                    .build();
            DroolsUtils.fireRules(kieContainer, null, GROUP_TEMPLATE_AGENDA_GROUP_NAME, groupTemplateFact);

            // 触发template规则，生成要绑定的局部变量，获取模板信息列表
            TemplateFact templateFact = TemplateFact.builder()
                    .pathGroupTemplate(groupTemplateFact.getPathGroupTemplate())
                    .contentGroupTemplate(groupTemplateFact.getContentGroupTemplate())
                    .inFileInfo(inFileInfo)
                    .globalVariableMap(globalVariableFact.getVariableMap())
                    .ruleOptionsMap(templateOptions.getRule())
                    .build();
            DroolsUtils.fireRules(kieContainer, null, TEMPLATE_AGENDA_GROUP_NAME, templateFact);

            // 遍历模板信息列表
            for (TemplateInfo templateInfo : templateFact.getTemplates()) {
                // 触发bindings规则，绑定变量到模板中
                DroolsUtils.fireRules(kieContainer, null, BINDINGS_AGENDA_GROUP_NAME, BindingsFact.builder()
                        .globalVariableMap(globalVariableFact.getVariableMap())
                        .templateInfo(templateInfo)
                        .build());

                // 触发render规则，根据模板渲染输出文件
                DroolsUtils.fireRules(kieContainer, null, RENDER_AGENDA_GROUP_NAME, RenderFact.builder()
                        .templateInfo(templateInfo)
                        .outFileInfos(outFileInfos)
                        .build());
            }
        }
        return outFileInfos;
    }

    /**
     * 创建GroupTemplates的Map列表
     *
     * @param groupTemplatesOptions 创建GroupTemplate的Map列表的选项
     * @return GroupTemplate的Map列表
     */
    private static Map<String, GroupTemplate> createGroupTemplates(Map<String, Properties> groupTemplatesOptions) throws IOException {
        Map<String, GroupTemplate>   groupTemplates = new HashMap<>();
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();

        // 如果未设置default的配置，则创建key为default的groupTemplate
        if (groupTemplatesOptions == null || groupTemplatesOptions.isEmpty()
                || !groupTemplatesOptions.containsKey("default")) {
            groupTemplates.put("default", new GroupTemplate(resourceLoader, new Configuration()));
        }

        // 通过groupTemplate选项中的配置创建groupTemplate
        if (groupTemplatesOptions != null) {
            for (Map.Entry<String, Properties> groupTemplateConfig : groupTemplatesOptions.entrySet()) {
                groupTemplates.put(groupTemplateConfig.getKey(), new GroupTemplate(resourceLoader,
                        new Configuration(groupTemplateConfig.getValue())));
            }
        }

        return groupTemplates;
    }

}
