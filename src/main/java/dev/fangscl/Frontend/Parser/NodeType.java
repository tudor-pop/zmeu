package dev.fangscl.Frontend.Parser;

public enum NodeType {
    Program,
    ExpressionStatement,
    BlockStatement,
    IfStatement,
    FunctionDeclarationStatement,
    LambdaExpression,
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
