package com.github.codgen.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfo {
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件内容
     */
    private String content;
}
