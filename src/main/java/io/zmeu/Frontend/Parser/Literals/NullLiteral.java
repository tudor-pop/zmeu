package io.zmeu.Frontend.Parser.Literals;

import io.zmeu.Frontend.Parser.Expressions.Visitor;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.api.types.Types;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * NumericLiteral
 *      : NUMBER
 *      ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NullLiteral extends Literal {
    private static final String value = "null";

    private NullLiteral() {
        this.kind = NodeType.NullLiteral;
    }

    public static Literal of() {
        return new NullLiteral();
    }

    @Override
    public Object getVal() {
        return value;
    }

    @Override
    public Types type() {
        return Types.Null;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
