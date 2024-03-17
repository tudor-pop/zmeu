package io.zmeu.Frontend.Parser;

public enum NodeType {
    Program,
    ExpressionStatement,
    BlockStatement,
    IfStatement,
    FunctionDeclaration,
    InitDeclaration,
    SchemaDeclaration,
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
    Path,
    BinaryExpression,
    LogicalExpression,
    GroupExpression,
    UnaryExpression,
    AssignmentExpression,
    ThisExpression,
    ResourceExpression,
    MemberExpression,
    CallExpression
}
