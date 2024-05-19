package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Expressions.Expression;

public class TypeError extends RuntimeException {
    public TypeError(Expression t1, Expression t2) {
        super("Expression " + t1.getKind().name() + " but expected " + t2.getKind().name());
    }

    public TypeError(Expression t1) {
        super("Expression is of type: " + t1.getKind().name());
    }
}
