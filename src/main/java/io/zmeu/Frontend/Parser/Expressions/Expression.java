package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.Literal;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;
import io.zmeu.TypeChecker.Types.Type;
import io.zmeu.Visitors.Visitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public sealed abstract class Expression permits AssignmentExpression, BinaryExpression, CallExpression,
        ErrorExpression, GroupExpression, LogicalExpression,
        MemberExpression, ThisExpression, UnaryExpression,
        VariableDeclaration, Identifier, Literal,
        BlockExpression, LambdaExpression, Type {


    public abstract <R> R accept(Visitor<R> visitor);

}
