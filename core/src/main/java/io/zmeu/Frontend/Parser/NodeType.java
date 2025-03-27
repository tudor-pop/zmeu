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

    Type,
    EmptyStatement,
    //    NullLiteral,
    NumberLiteral,
    StringLiteral,
    BooleanLiteral,
    NullLiteral,
    //    BooleanLiteral,
    Identifier,
    BinaryExpression,
    LogicalExpression,
    GroupExpression,
    UnaryExpression,
    AssignmentExpression,
    ThisExpression,
    ResourceExpression,
    ModuleExpression,
    MemberExpression,
    CallExpression
}
