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

public class Codgen {
    public static void gen(GenOptions options) throws SQLException, IOException {
        JdbcUtils.DbMeta dbMeta = JdbcUtils.getDbMeta(options.getJdbc().getConnect(), options.getJdbc().getTableName());
        System.out.printf("database meta: %s%n", JacksonUtils.serializeWithPretty(dbMeta));

        //初始化代码
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration                configuration;
        if (options.getBeetl() != null && !options.getBeetl().isEmpty())
            configuration = new Configuration(MapUtils.map2Props(options.getBeetl()));
        else
            configuration = new Configuration();
        GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, configuration);
        //获取模板
        Template template = groupTemplate.getTemplate("hello,${name}");
        template.binding("name", "beetl");
        //渲染结果
        String str = template.render();
        System.out.println(str);
    }
}
