package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;

public abstract class Literal extends Expression {
    public static Literal of(int value) {
        return new NumericLiteral(value);
    }

    public static Literal of(float value) {
        return new NumericLiteral(value);
    }

    public static Literal of(double value) {
        return new NumericLiteral(value);
    }

    public static StringLiteral of(String value) {
        return new StringLiteral(value);
    }

    abstract public Object getVal();
}
