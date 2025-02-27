package io.github.codgen.java.cmp;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeIteratorComponent;

import io.github.codgen.java.to.FileScannerTo;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.file.FileSearcher;

/**
 * 文件扫描器组件
 * 扫描文件获取文件列表
 */
@Slf4j
@LiteflowComponent("fileScanner")
public class FileScannerCmp extends NodeIteratorComponent {

    @Override
    public Iterator<?> processIterator() throws Exception {
        FileScannerTo fileScannerTo = this.getCmpData(FileScannerTo.class);
        log.info("文件扫描参数: {}", fileScannerTo);
        Path       fileScanDir = Path.of(fileScannerTo.getDir()).toRealPath();
        List<Path> files       = FileSearcher.findFiles(fileScanDir, fileScannerTo.getPattern());
        log.info("文件扫描结果: {}", files);
        return files.iterator();
    }
}
