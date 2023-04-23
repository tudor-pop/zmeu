package dev.fangscl.Frontend.Parser;

public enum NodeType {
    Program,
    ExpressionStatement,
    BlockExpression,
    IfStatement,
    FunctionDeclarationStatement,
    ReturnStatement,
    WhileStatement,
    ForStatement,

    //    Variables
    VariableStatement,
    VariableDeclaration,

    EmptyStatement,
    //    NullLiteral,
    DecimalLiteral,
    IntegerLiteral,
    StringLiteral,
    BooleanLiteral,
    NullLiteral,
    //    BooleanLiteral,
    Identifier,
    BinaryExpression,
    LogicalExpression,
    UnaryExpression,
    AssignmentExpression,
    MemberExpression,
    CallExpression
}
