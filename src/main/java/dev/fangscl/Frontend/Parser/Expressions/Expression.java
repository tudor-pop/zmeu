package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.Statements.Statement;

/**
 * Order of preccedence
 * AssignmentExpression
 * MemberExpression
 * FunctionCall
 * LogicalExpression
 * ComparisonExpression
 * AdditiveExpression
 * MultiplicativeExpression
 * LiteralExpression - Identity, integer, decimal
 * <p>
 * Expression
 * : Literal
 * ;
 */
public abstract class Expression extends Statement {


}
