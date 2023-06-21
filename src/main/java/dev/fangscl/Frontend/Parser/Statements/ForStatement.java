package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Frontend.Parser.Expressions.Expression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <p>
 * BlockStatement
 * : '{' OptionalStatementList '}'
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class ForStatement extends Statement {
    private Expression test;
    @Nullable
    private Statement init;
    private Expression update;
    private Statement body;


    private ForStatement(Expression test, @Nullable VariableStatement init, @Nullable Statement body,
                         @Nullable Expression update) {
        this.kind = NodeType.IfStatement;
        this.update = update;
        this.init = init;
        this.test = test;
        this.body = body;
    }

    public ForStatement() {
        this.kind = NodeType.BlockStatement;
    }

    public static Statement of() {
        return new ForStatement();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public boolean hasInit() {
        return init != null;
    }

    public List<Statement> discardBlock() {
        if (body instanceof ExpressionStatement statement) {
            if (statement.getStatement() instanceof BlockExpression expression) {
                return expression.getExpression();
            }
        }
        return null;
    }

    public boolean isBodyBlock() {
        if (body instanceof ExpressionStatement statement) {
            return statement.getStatement() instanceof BlockExpression;
        }
        return false;
    }

}
