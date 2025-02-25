package io.github.codgen.java.to;

import lombok.Data;

@Data
public class FileScanTo {
    /**
     * 源目录路径
     */
    private String src;
    /**
     * 目的目录路径
     */
    private String target;
    /**
     * 文件名
     */
    private String fileName;
}
