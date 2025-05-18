package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.ModuleExpression;
import io.zmeu.Frontend.Parser.Expressions.ResourceExpression;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Visitors.Visitor;
import lombok.Data;

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
public abstract sealed class Statement permits ModuleExpression,
        ResourceExpression, Program,
        EmptyStatement, ExpressionStatement,
        ForStatement, FunctionDeclaration,
        IfStatement, InitStatement, ReturnStatement,
        SchemaDeclaration, VariableStatement, WhileStatement {

    public abstract  <R> R accept(Visitor<R> visitor);
}
