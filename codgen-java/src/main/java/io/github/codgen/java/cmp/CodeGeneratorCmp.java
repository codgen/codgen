package io.github.codgen.java.cmp;

import java.nio.file.Path;
import java.util.Map;

import org.beetl.core.Template;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.ctx.BeetlCtx;
import io.github.codgen.java.ctx.CodeParserCtx;
import io.github.codgen.java.to.CodeGeneratorTo;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.file.FileUtils;

/**
 * 代码生成器组件
 */
@Slf4j
@LiteflowComponent("codeGenerator")
public class CodeGeneratorCmp extends NodeComponent {

    @Override
    public void process() throws Exception {
        CodeGeneratorTo     codeGeneratorTo = this.getCmpData(CodeGeneratorTo.class);
        BeetlCtx            beetlCtx        = this.getContextBean(BeetlCtx.class);
        CodeParserCtx       codeParserCtx   = this.getContextBean(CodeParserCtx.class);

        String              btlContent      = FileUtils.readToString(Path.of(codeGeneratorTo.getBtl()).toFile());
        Map<String, Object> bindings        = codeParserCtx.getBindings();
        Template            template        = beetlCtx.getGroupTemplate().getTemplate(btlContent);
        template.binding(bindings);
        String renderContent = template.render();
        System.out.println(renderContent);
    }
}