package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.ResourceExpression;
import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Program;

public interface Visitor<R> {
    R eval(Program program);

    R eval(Statement statement);

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

    R eval(VariableDeclaration statement);

    R eval(Identifier statement);

    /**
     * An instance of a Schema is an Environment!
     * the 'parent' component of the instance environment is set to the class environment making class members accessible
     */
    R eval(ResourceExpression expression);

}
