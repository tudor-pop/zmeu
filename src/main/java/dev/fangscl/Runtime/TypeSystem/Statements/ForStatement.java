package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

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
    public String toSExpression() {
        return test.toSExpression() + body.toSExpression();
    }
}
