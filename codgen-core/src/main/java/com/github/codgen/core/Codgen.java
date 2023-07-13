package com.github.codgen.core;

import com.github.codgen.core.drools.fact.BindingsFact;
import com.github.codgen.core.drools.fact.GlobalFact;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import rebue.wheel.core.MapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Codgen {
    /**
     * drools会话名称
     */
    private static final String KSESSION_NAME              = "codgen";
    /**
     * 全局变量的议程分组名称
     */
    private static final String GLOBAL_AGENDA_GROUP_NAME   = "globals";
    /**
     * 绑定变量的议程分组名称
     */
    private static final String BINDINGS_AGENDA_GROUP_NAME = "bindings";

    /**
     * 生成
     *
     * @param inFileInfos 输入文件信息列表
     * @param drls        drools规则文件内容列表
     * @param genOptions  生成的配置选项
     */
    public static void gen(List<FileInfo> inFileInfos, Map<String, String> drls, GenOptions genOptions) throws IOException {
        // 初始化drools
        KieContainer kieContainer = null;
        if (drls != null) {
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem()
                    // XXX kmodule.xml文件的路必须是 src/main/resources/META-INF/kmodule.xml
                    // 可以用 .writeKModuleXML(""" 替换
                    .write("src/main/resources/META-INF/kmodule.xml", """   
                            <?xml version="1.0" encoding="UTF-8"?>
                            <kmodule xmlns="http://www.drools.org/xsd/kmodule">
                              <kbase default="true">
                                  <ksession name="codgen" default="true" />
                              </kbase>
                            </kmodule>
                            """);
            for (Map.Entry<String, String> drl : drls.entrySet()) {
                // XXX 规则文件必须放在 src/main/resources/ 路径下
                kieFileSystem.write("src/main/resources/" + drl.getKey(), drl.getValue());
            }
            kieServices.newKieBuilder(kieFileSystem).buildAll();
            kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        }

        // 改变global
        GlobalFact globalFact;
        if (kieContainer != null) {
            KieSession kieSession = kieContainer.newKieSession();
            kieSession.getAgenda().getAgendaGroup(GLOBAL_AGENDA_GROUP_NAME).setFocus();
            globalFact = GlobalFact.builder()
                    .globals(new HashMap<>())
                    .globalOptions(genOptions.getGlobal())
                    .build();
            kieSession.insert(globalFact);
            int firedRulesCount = kieSession.fireAllRules();
            System.out.printf("触发执行了改变global的规则数为%d%n", firedRulesCount);
            kieSession.dispose();
        }

//        // 读取数据库信息
//        Map<String, JdbcUtils.DbMeta> dbMetas = new HashMap<>();
//        if (genOptions.getJdbc() != null && !genOptions.getJdbc().isEmpty()) {
//            for (Map.Entry<String, GenOptions.JdbcOptions> jdbc : genOptions.getJdbc().entrySet()) {
//                JdbcUtils.DbMeta dbMeta = JdbcUtils.getDbMeta(jdbc.getValue().getConnect(), jdbc.getValue().getTableName());
//                dbMetas.put(jdbc.getKey(), dbMeta);
//            }
//        }
//        System.out.printf("database meta: %s%n", JacksonUtils.serializeWithPretty(dbMetas));

        // 初始化groupTemplate
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Map<String, GroupTemplate>   groupTemplates = new HashMap<>();
        if (genOptions.getGroupTemplate() == null || genOptions.getGroupTemplate().isEmpty()) {
            groupTemplates.put("default", new GroupTemplate(resourceLoader, new Configuration()));
        } else {
            for (Map.Entry<String, Map<String, ?>> groupTemplateConfig : genOptions.getGroupTemplate().entrySet()) {
                groupTemplates.put(groupTemplateConfig.getKey(), new GroupTemplate(resourceLoader,
                        new Configuration(MapUtils.map2Props(groupTemplateConfig.getValue()))));
            }
        }

        // 获取配置选项中的bindings
        Map<String, Map<String, ?>> bindingsMap = new HashMap<>();
        if (genOptions.getBindings() != null && !genOptions.getBindings().isEmpty()) {
            bindingsMap.putAll(genOptions.getBindings());
        }

        // 输出文件信息列表
        List<FileInfo> outFileInfos  = new LinkedList<>();
        GroupTemplate  groupTemplate = groupTemplates.get("default");
        for (FileInfo inFileInfo : inFileInfos) {
            // 获取默认的bindings
            Map<String, ?> bindings = bindingsMap.get("default");
            // 获取路径的模板
            Template pathTemplate = groupTemplate.getTemplate(inFileInfo.getPath());
            // 获取内容的模板
            Template contentTemplate = groupTemplate.getTemplate(inFileInfo.getContent());
            // 执行规则引擎自定义绑定变量
            if (kieContainer != null) {
                KieSession kieSession = kieContainer.newKieSession(KSESSION_NAME);
                kieSession.getAgenda().getAgendaGroup(BINDINGS_AGENDA_GROUP_NAME).setFocus();
                BindingsFact bindingsFact = BindingsFact.builder()
                        .filePath(inFileInfo.getPath())
                        .bindings(bindings)
                        .build();
                kieSession.insert(bindingsFact);
                int firedRulesCount = kieSession.fireAllRules();
                System.out.printf("触发执行了改变bindings的规则数为%d%n", firedRulesCount);
                kieSession.dispose();
            }
            // 绑定bindings到模板
            for (Map.Entry<String, ?> binding : bindings.entrySet()) {
                pathTemplate.binding(binding.getKey(), binding.getValue());
                contentTemplate.binding(binding.getKey(), binding.getValue());
            }
            // 添加渲染结果
            outFileInfos.add(FileInfo.builder()
                    .path(pathTemplate.render())            // 渲染路径结果
                    .content(contentTemplate.render())      // 渲染内容结果
                    .build());
        }
        System.out.println(outFileInfos);
    }
}
