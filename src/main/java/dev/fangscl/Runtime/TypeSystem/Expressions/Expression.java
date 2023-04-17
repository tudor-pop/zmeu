package dev.fangscl.Runtime.TypeSystem.Expressions;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Runtime.TypeSystem.Statements.Statement;

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
