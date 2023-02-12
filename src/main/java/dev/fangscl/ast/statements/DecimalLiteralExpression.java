package dev.fangscl.ast.statements;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecimalLiteralExpression extends Expression {
    private double value;

    public DecimalLiteralExpression() {
        this.kind = NodeType.NumericLiteral;
    }

    public DecimalLiteralExpression(double value) {
        this();
        this.value = value;
    }
    public DecimalLiteralExpression(String value) {
        this();
        this.value = Double.parseDouble(value);
    }
}
