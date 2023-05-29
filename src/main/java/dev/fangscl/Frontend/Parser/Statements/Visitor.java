package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Program;

public interface Visitor<R> {
    R visit(Statement expression);

    R visit(Program expression);

}
