package io.github.codgen.java.to;

import lombok.Data;

@Data
public class FileScannerTo {
    /**
     * 扫描目录
     */
    private String dir;
    /**
     * 匹配模式
     */
    private String pattern;
}
