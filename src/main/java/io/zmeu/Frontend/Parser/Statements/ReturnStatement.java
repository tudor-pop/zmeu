package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReturnStatement extends Statement {
    private Identifier name;
    private Statement argument;

    public ReturnStatement(@Nullable Statement argument) {
        this();
        this.argument = argument;
    }
    public ReturnStatement(@Nullable Expression argument) {
        this(ExpressionStatement.expressionStatement(argument));
    }

    public ReturnStatement() {
        this.kind = NodeType.ReturnStatement;
    }


    public static Statement funReturn(Expression body) {
        return new ReturnStatement(body);
    }

    public static Statement funReturn(Statement body) {
        return new ReturnStatement(body);
    }

    public static Statement funReturn(int value) {
        return new ReturnStatement(NumberLiteral.of(value));
    }

    public static Statement funReturn(double value) {
        return new ReturnStatement(NumberLiteral.of(value));
    }

    public static Statement funReturn() {
        return new ReturnStatement();
    }

    public static Statement funReturn(float value) {
        return new ReturnStatement(NumberLiteral.of(value));
    }

    public static Statement funReturn(String value) {
        return new ReturnStatement(StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public boolean hasArgument() {
        return argument != null;
    }
}
