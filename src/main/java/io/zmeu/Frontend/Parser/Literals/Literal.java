package io.zmeu.Frontend.Parser.Literals;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.api.types.Types;

public abstract class Literal extends Expression {

    abstract public Object getVal();

    abstract public Types type();
}
