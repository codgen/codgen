package com.github.codgen.core.drools.fact;

import com.github.codgen.core.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.core.Template;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenderFact {
    /**
     * 模板文件信息(其中的路径可用于判断激活哪个议程)
     */
    private FileInfo       templateFileInfo;
    /**
     * 路径模板
     */
    private Template       pathTemplate;
    /**
     * 内容模板
     */
    private Template       contentTemplate;
    /**
     * 输出文件信息列表(在原有列表添加文件信息后返回给主程用，可以添加多个文件信息)
     */
    private List<FileInfo> outFileInfos;
}
