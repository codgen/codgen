package com.github.codgen.core.drools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * drools文件内容列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DroolsFiles {
    /**
     * 规则文件的内容列表
     */
    private Map<String, String> rules;
}
