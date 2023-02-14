package dev.fangscl.ast.Statements.Expressions;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecimalExpression extends Expression {
    private double value;

    public DecimalExpression() {
        this.kind = NodeType.NumericLiteral;
    }

    public DecimalExpression(double value) {
        this();
        this.value = value;
    }
    public DecimalExpression(String value) {
        this();
        this.value = Double.parseDouble(value);
    }
}
