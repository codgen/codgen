package com.github.codgen.core.drools.fact;

import com.github.codgen.core.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenderFact {
    /**
     * 模板信息
     */
    private TemplateInfo   templateInfo;
    /**
     * 输出文件信息列表(在原有列表添加文件信息后返回给主程用，可以添加多个文件信息)
     */
    private List<FileInfo> outFileInfos;
}
