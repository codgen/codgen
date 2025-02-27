package io.github.codgen.java.ctx;

import java.util.Map;

import lombok.Data;

@Data
public class CodeParserCtx {
    private Map<String, Object> bindings;
}
