package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Statements.Statement;

/**
 * Expression
 * : AssignmentExpression
 * | BlockStatement
 * ;
 */
public abstract class Expression extends Statement {

    public abstract <R> R accept(Visitor<R> visitor);

}
