package com.github.codgen.core;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import rebue.wheel.core.JdbcUtils;
import rebue.wheel.core.MapUtils;
import rebue.wheel.serialization.jackson.JacksonUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Codgen {
    public static void gen(GenOptions options) throws SQLException, IOException {
        Map<String, JdbcUtils.DbMeta> dbMetas = new HashMap<>();
        if (options.getJdbc() != null && !options.getJdbc().isEmpty()) {
            for (Map.Entry<String, GenOptions.JdbcOptions> jdbc : options.getJdbc().entrySet()) {
                JdbcUtils.DbMeta dbMeta = JdbcUtils.getDbMeta(jdbc.getValue().getConnect(), jdbc.getValue().getTableName());
                dbMetas.put(jdbc.getKey(), dbMeta);
            }
        }
        System.out.printf("database meta: %s%n", JacksonUtils.serializeWithPretty(dbMetas));

        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Map<String, GroupTemplate> groupTemplates = new HashMap<>();
        if (options.getGroupTemplate() == null || options.getGroupTemplate().isEmpty()) {
            groupTemplates.put("default", new GroupTemplate(resourceLoader, new Configuration()));
        } else {
            for (Map.Entry<String, Map<String, ?>> groupTemplateConfig : options.getGroupTemplate().entrySet()) {
                groupTemplates.put(groupTemplateConfig.getKey(), new GroupTemplate(resourceLoader,
                        new Configuration(MapUtils.map2Props(groupTemplateConfig.getValue()))));
            }
        }

        Map<String, Map<String, ?>> bindingsMap = new HashMap<>();
        if (options.getBinding() != null && !options.getBinding().isEmpty()) {
            bindingsMap.putAll(options.getBinding());
        }

        GroupTemplate groupTemplate = groupTemplates.get("default");
        //获取模板
        Template template = groupTemplate.getTemplate("hello, ${projectName}");
        Map<String, ?> bindings = bindingsMap.get("default");
        for (Map.Entry<String, ?> binding : bindings.entrySet()) {
            template.binding(binding.getKey(), binding.getValue());
        }
        //渲染结果
        String str = template.render();
        System.out.println(str);
    }
}
