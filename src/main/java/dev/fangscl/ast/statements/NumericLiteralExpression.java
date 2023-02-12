package dev.fangscl.ast.statements;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NumericLiteralExpression extends Expression {
    private Number value;

    public NumericLiteralExpression() {
        this.kind = NodeType.NumericLiteral;
    }
}
