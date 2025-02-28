package io.github.codgen.java.cmp;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.ctx.CodeGeneratorCtx;
import io.github.codgen.java.ctx.CodeParserCtx;
import io.github.codgen.java.to.FileWriterTo;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.StrUtils;
import rebue.wheel.core.file.FileUtils;

/**
 * Ctrl类的文件书写器
 */
@Slf4j
public class FileWriterCmp extends NodeComponent {
    @Override
    public void process() throws Exception {
        FileWriterTo        fileWriterTo     = this.getCmpData(FileWriterTo.class);
        CodeParserCtx       codeParserCtx    = this.getContextBean(CodeParserCtx.class);
        CodeGeneratorCtx    codeGeneratorCtx = this.getContextBean(CodeGeneratorCtx.class);
        Map<String, Object> bindings         = codeParserCtx.getBindings();

        String              packageName      = fileWriterTo.getPackageName();
        String              suffix           = StrUtils.capitalize(packageName);
        String              moduleName       = bindings.get("moduleName").toString();
        String              capModuleName    = bindings.get("capModuleName").toString();
        String              entityName       = bindings.get("entityName").toString();
        Path                targetPath       = Path.of(fileWriterTo.getTarget());
        Path                outDirPath       = targetPath.resolve(Path.of(packageName, moduleName));
        File                outDir           = outDirPath.toFile();
        // 如果目录不存在则创建
        outDir.mkdirs();

        File file = outDirPath.resolve(capModuleName + entityName + suffix + ".ts").toFile();
        log.info("正在生成文件：{}", file.getAbsolutePath());
        FileUtils.writeToFile(file, codeGeneratorCtx.getContent());
    }
}