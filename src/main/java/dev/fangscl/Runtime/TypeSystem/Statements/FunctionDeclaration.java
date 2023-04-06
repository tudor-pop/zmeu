package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * FunctionDeclaration
 * : fun Identifier ( Arguments ) BlockStatement
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionDeclaration extends Statement {
    private Statement expression;

    public FunctionDeclaration(Statement expression) {
        this.kind = NodeType.BlockStatement;
        this.expression = expression;
    }

    public static Statement of(Expression expression) {
        return new FunctionDeclaration(expression);
    }

    public static Statement of(int value) {
        return new FunctionDeclaration(Literal.of(value));
    }

    public static Statement of(double value) {
        return new FunctionDeclaration(Literal.of(value));
    }

    public static Statement of(float value) {
        return new FunctionDeclaration(Literal.of(value));
    }

    public static Statement of(String value) {
        return new FunctionDeclaration(Literal.of(value));
    }

    @Override
    public String toSExpression() {
        return  expression.toSExpression() ;
    }
}
