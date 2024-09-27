package io.zmeu.Visitors;

import io.zmeu.Frontend.Lexical.Resolver;
import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.Parser.Types.Type;
import io.zmeu.TypeChecker.TypeChecker;
import io.zmeu.Runtime.Interpreter;

public sealed interface Visitor<R>
        permits Resolver, TypeChecker, Interpreter, AstPrinter, LanguageAstPrinter, SyntaxPrinter {

    default R eval(Expression expr) {
        return switch (expr) {
            case BinaryExpression expression -> eval(expression);
            case AssignmentExpression expression -> eval(expression);
            case CallExpression expression -> eval((CallExpression<Expression>) expression);
            case ErrorExpression expression -> eval(expression);
            case GroupExpression expression -> eval(expression);
            case LogicalExpression expression -> eval(expression);
            case MemberExpression expression -> eval(expression);
            case ThisExpression expression -> eval(expression);
            case UnaryExpression expression -> eval(expression);
            case VariableDeclaration expression -> eval(expression);
            case Identifier identifier -> eval(identifier);
            case Literal literal -> eval(literal);
            case BlockExpression expression -> eval(expression);
            case LambdaExpression expression -> eval(expression);
            case Type type -> eval(type);
        };
    }

    default R eval(Literal expression) {
        return switch (expression) {
            case NumberLiteral number -> eval(number);
            case StringLiteral string -> eval(string);
            case BooleanLiteral bool -> eval(bool);
            case NullLiteral nullliteral -> eval(nullliteral);
            default -> throw new IllegalStateException("Unexpected value: " + expression);
        };
    }

    R eval(NumberLiteral expression);

    R eval(BooleanLiteral expression);

    R eval(Identifier expression);

    R eval(NullLiteral expression);

    R eval(StringLiteral expression);

    R eval(LambdaExpression expression);

    R eval(BlockExpression expression);

    R eval(GroupExpression expression);

    R eval(BinaryExpression expression);

    R eval(CallExpression<Expression> expression);

    R eval(ErrorExpression expression);

    R eval(LogicalExpression expression);

    R eval(MemberExpression expression);

    R eval(ThisExpression expression);

    R eval(UnaryExpression expression);

    R eval(VariableDeclaration expression);

    R eval(AssignmentExpression expression);

    R eval(float expression);

    R eval(double expression);

    R eval(int expression);

    R eval(boolean expression);

    R eval(String expression);

    R eval(Program program);

    R eval(Statement statement);

    R eval(Type type);

    /**
     * InitStatement
     * Syntactic sugar for a function
     */
    R eval(InitStatement statement);

    R eval(FunctionDeclaration statement);

    R eval(ExpressionStatement statement);

    R eval(VariableStatement statement);

    R eval(IfStatement statement);

    R eval(WhileStatement statement);

    R eval(ForStatement statement);

    R eval(SchemaDeclaration statement);

    R eval(ReturnStatement statement);

    /**
     * An instance of a Schema is an Environment!
     * the 'parent' component of the instance environment is set to the class environment making class members accessible
     */
    R eval(ResourceExpression expression);
}
