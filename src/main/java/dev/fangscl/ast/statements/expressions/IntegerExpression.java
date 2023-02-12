package dev.fangscl.ast.statements.expressions;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IntegerExpression extends Expression {
    private int value;

    public IntegerExpression() {
        this.kind = NodeType.NumericLiteral;
    }

    public IntegerExpression(int value) {
        this();
        this.value = value;
    }
    public IntegerExpression(String value) {
        this();
        this.value = Integer.parseInt(value);
    }
}
