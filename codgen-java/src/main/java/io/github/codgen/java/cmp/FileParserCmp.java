package io.github.codgen.java.cmp;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.ctx.FileParserCtx;
import io.github.codgen.java.to.FileParserTo;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件解析器组件
 * 解析 Java 文件并获取编译单元，用于解析 Java 文件中的类、方法、字段、注释等
 */
@Slf4j
@LiteflowComponent("fileParser")
public class FileParserCmp extends NodeComponent {

    @Override
    public void process() throws Exception {
        FileParserTo fileParserTo = this.getCmpData(FileParserTo.class);
        Path         filePath     = this.getCurrLoopObj();
        log.info("文件解析参数: {}-{}", fileParserTo, filePath);
        StaticJavaParser.getParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.valueOf(fileParserTo.getJavaVersion()));
        FileParserCtx ctx = this.getContextBean(FileParserCtx.class);
        ctx.setCompilationUnit(StaticJavaParser.parse(Files.newInputStream(filePath)));
    }
}
