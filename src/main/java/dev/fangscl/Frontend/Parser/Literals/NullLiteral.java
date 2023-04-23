package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.NodeType;
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
    public String toSExpression() {
        return value;
    }


    @Override
    public Object getVal() {
        return value;
    }
}