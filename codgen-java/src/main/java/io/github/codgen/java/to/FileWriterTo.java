package io.github.codgen.java.to;

import lombok.Data;

@Data
public class FileWriterTo {
    /**
     * 包名
     * 用于配置第一个目录以及后缀
     */
    private String packageName;
    /**
     * 生成目标路径
     */
    private String target;
}
