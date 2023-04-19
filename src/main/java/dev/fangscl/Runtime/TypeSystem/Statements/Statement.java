package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Statements do not return a values as opposed to expressions
 * example:
 * var x = 10 -> does not return a value, most repl return undefined
 * x = 10 -> no longer a statement is an assignment expression which returns 10
 * <p>
 * <p>
 * Statement
 * : ExpressionStatement
 * | BlockStatement
 * | EmptyStatement
 * | IfStatement
 * | WhileStatement
 * | ForStatement
 * | FunctionDeclaration
 * ;
 */
@Data
public abstract class Statement {
    protected NodeType kind;

    public abstract String toSExpression();

    public boolean is(NodeType type) {
        return kind == type;
    }

    public boolean is(NodeType... type) {
        return ArrayUtils.contains(type, kind);
    }
}

