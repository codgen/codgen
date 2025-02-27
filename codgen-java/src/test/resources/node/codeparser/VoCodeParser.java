import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.ctx.FileParserCtx;

/**
 * Vo类的代码解析器
 * 解析Vo类的代码，获取其中的类、方法、字段、注释等
 */
public class VoCodeParser extends NodeComponent {
    private static final Logger log = LoggerFactory.getLogger(VoCodeParser.class);

    @Override
    public void process() throws Exception {
        FileParserCtx   ctx             = this.getContextBean(FileParserCtx.class);
        CompilationUnit compilationUnit = ctx.getCompilationUnit();
        log.info("{}", compilationUnit);
    }
}