package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.Literal;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.LambdaExpression;
import io.zmeu.Frontend.Parser.Types.Type;
import io.zmeu.Visitors.Visitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;

@Getter
@ToString
@EqualsAndHashCode
public sealed abstract class Expression permits AssignmentExpression, BinaryExpression, CallExpression,
        ErrorExpression, GroupExpression, LogicalExpression,
        MemberExpression, ThisExpression, UnaryExpression,
        VariableDeclaration, Identifier, Literal,
        BlockExpression, LambdaExpression, Type {

    @Setter
    protected NodeType kind;

    public boolean is(NodeType type) {
        return kind == type;
    }

    public boolean is(NodeType... type) {
        return ArrayUtils.contains(type, kind);
    }


    public abstract <R> R accept(Visitor<R> visitor);

}
