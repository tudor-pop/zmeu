package dev.fangscl.ast.TypeSystem.Expressions;

import dev.fangscl.ast.TypeSystem.Base.Expression;
import dev.fangscl.ast.TypeSystem.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BinaryExpression extends Expression {
    private Expression left;
    private Expression right;
    private String operator;

    public BinaryExpression() {
        this.kind = NodeType.BinaryExpression;
    }

    public BinaryExpression(Expression left, Expression right, String operator) {
        this();
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
