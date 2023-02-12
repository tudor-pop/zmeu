package dev.fangscl.ast.statements;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerLiteralExpression extends Expression {
    private int value;

    public IntegerLiteralExpression() {
        this.kind = NodeType.NumericLiteral;
    }

    public IntegerLiteralExpression(int value) {
        this();
        this.value = value;
    }
    public IntegerLiteralExpression(String value) {
        this();
        this.value = Integer.parseInt(value);
    }
}
