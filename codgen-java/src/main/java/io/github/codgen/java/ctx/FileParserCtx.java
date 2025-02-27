package io.github.codgen.java.ctx;

import com.github.javaparser.ast.CompilationUnit;

import lombok.Data;

@Data
public class FileParserCtx {
    private CompilationUnit compilationUnit;
}
