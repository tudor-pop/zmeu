package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.ResourceExpression;
import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Program;

public interface Visitor<R> {
    R eval(Program program);

    R eval(Statement statement);

    R eval(InitStatement statement);

    /**
     * InitStatement
     * Syntactic sugar for a function
     */
    R eval(FunctionDeclaration statement);

    R eval(ExpressionStatement statement);

    R eval(VariableStatement statement);

    R eval(IfStatement statement);

    R eval(WhileStatement statement);

    R eval(ForStatement statement);

    R eval(TypeDeclaration statement);

    R eval(VariableDeclaration statement);

    R eval(Identifier statement);

    /**
     * An instance of a Schema is an Environment!
     * the 'parent' component of the instance environment is set to the class environment making class members accessible
     */
    R eval(ResourceExpression expression);

}
