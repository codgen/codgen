package io.github.codgen.java.cmp;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.to.FileScanTo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LiteflowComponent("fileScan")
public class FileScanCmp extends NodeComponent {
    @Override
    public void process() {
        FileScanTo fileScanTo = this.getCmpData(FileScanTo.class);
        log.info("文件扫描: {}", fileScanTo);
    }
}
