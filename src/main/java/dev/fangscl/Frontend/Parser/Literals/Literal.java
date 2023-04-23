package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.Expressions.Expression;

public abstract class Literal extends Expression {

    abstract public Object getVal();
}
