package io.github.codgen.java.to;

import lombok.Data;

@Data
public class CodeGeneratorTo {
    /**
     * 模板路径
     */
    private String btl;
    /**
     * 生成目标路径
     */
    private String target;
}
