package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.NodeType;

public sealed abstract class Type extends Expression
        permits ValueType, ReferenceType {

    public Type() {
        this.kind = NodeType.Type;
    }

    public  static Type of(String string) {
        var res = ValueType.of(string);
        if (res == null) {
            return ReferenceType.of(string);
        }
        return res;
    }

    public abstract String value();
}
