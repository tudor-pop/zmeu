package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A string literal has the form of: "hello" or empty string ""
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StringLiteral extends Expression {
    private String value;

    public StringLiteral() {
        this.kind = NodeType.StringLiteral;
    }

    public StringLiteral(String value) {
        this();
        this.value = value;
    }

}
