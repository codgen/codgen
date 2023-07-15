package com.github.codgen.core.drools;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Map;

public class DroolsUtils {
    private static KieContainer kieContainer;

    /**
     * 初始化drools
     *
     * @param drls drools规则文件内容列表
     */
    public static void init(Map<String, String> drls) {
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
//        kieServices.newKieBuilder(kieFileSystem).buildAll();
//        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

    /**
     * 触发规则
     *
     * @param agendaGroup 议程分组
     * @param fact        要传递给规则执行的参数
     * @return 触发执行的规则数
     */
    public static int fireRules(String agendaGroup, Object fact) {
        // 执行规则引擎自定义绑定变量
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();
        kieSession.insert(fact);
        int firedRulesCount = kieSession.fireAllRules();
        System.out.printf("触发执行了改变%s的规则数为%d%n", agendaGroup, firedRulesCount);
        kieSession.dispose();
        return firedRulesCount;
    }

}
