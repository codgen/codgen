package io.github.codgen.java.cmp;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.ctx.FileParserCtx;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件解析器组件
 * 解析 Java 文件并获取编译单元，用于解析 Java 文件中的类、方法、字段、注释等
 */
@Slf4j
public class FileParserCmp extends NodeComponent {
    static {
        StaticJavaParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
    }

    @Override
    public void process() throws Exception {
        Path filePath = this.getCurrLoopObj();
        log.info("文件解析开始: {}", filePath);
        FileParserCtx ctx = this.getContextBean(FileParserCtx.class);
        ctx.setCompilationUnit(StaticJavaParser.parse(Files.newInputStream(filePath)));
    }
}
