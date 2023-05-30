package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Program;

public interface Visitor<R> {
    R visit(Program program);

    R visit(Statement statement);

    R visit(InitStatement statement);

    /**
     * InitStatement
     * Syntactic sugar for a function
     */
    R visit(FunctionDeclaration statement);

    R visit(ExpressionStatement statement);

    R visit(VariableStatement statement);

    R visit(IfStatement statement);

    R visit(WhileStatement statement);

    R visit(ForStatement statement);

    R visit(SchemaDeclaration statement);

    R visit(VariableDeclaration statement);

    R visit(Identifier statement);

}
