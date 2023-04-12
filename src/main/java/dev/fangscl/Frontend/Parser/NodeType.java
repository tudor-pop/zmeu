package dev.fangscl.Frontend.Parser;

public enum NodeType {
    Program,
    ExpressionStatement,
    BlockStatement,

    //    Variables
    VariableStatement,
    VariableDeclaration,

    EmptyStatement,
    //    NullLiteral,
    DecimalLiteral,
    IntegerLiteral,
    StringLiteral,
    //    BooleanLiteral,
    Identifier,
    BinaryExpression,
    AssignmentExpression
}
