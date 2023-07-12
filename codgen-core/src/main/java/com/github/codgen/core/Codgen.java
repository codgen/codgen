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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Codgen {
    /**
     * 生成
     *
     * @param fileInfos 文件信息列表
     * @param options   生成的配置选项
     */
    public static void gen(List<FileInfo> fileInfos, GenOptions options) throws SQLException, IOException {
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

        // 获取配置选项中的bindings
        Map<String, Map<String, ?>> bindingsMap = new HashMap<>();
        if (options.getBinding() != null && !options.getBinding().isEmpty()) {
            bindingsMap.putAll(options.getBinding());
        }

        List<FileInfo> outFileInfos = new LinkedList<>();
        GroupTemplate groupTemplate = groupTemplates.get("default");
        for (FileInfo inFileInfo : fileInfos) {
            // 获取默认的bindings
            Map<String, ?> bindings = bindingsMap.get("default");
            // 获取路径的模板
            Template pathTemplate = groupTemplate.getTemplate(inFileInfo.getPath());
            // 获取内容的模板
            Template contentTemplate = groupTemplate.getTemplate(inFileInfo.getContent());
            // 绑定bindings到模板
            for (Map.Entry<String, ?> binding : bindings.entrySet()) {
                pathTemplate.binding(binding.getKey(), binding.getValue());
                contentTemplate.binding(binding.getKey(), binding.getValue());
            }
            outFileInfos.add(FileInfo.builder()
                    // 渲染路径结果
                    .path(pathTemplate.render())
                    // 渲染内容结果
                    .content(contentTemplate.render())
                    .build());
        }
        System.out.println(outFileInfos);
    }
}
