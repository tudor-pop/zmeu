package io.zmeu.Frontend.Parser.Literals;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.visitors.Visitor;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

/**
 * Acts as VariableExpression without creating a new node
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Identifier extends Expression {
    private String symbol;
    private Integer hops; // used to figure out the scope without creating extra classes

    public Identifier() {
        this.kind = NodeType.Identifier;
    }

    public Identifier(String symbol) {
        this();
        this.symbol = symbol;
    }

    public Identifier(Object symbol) {
        this();
        if (symbol instanceof String s) {
            this.symbol = s;
        }
    }

    public Identifier(boolean symbol) {
        this();
        this.symbol = String.valueOf(symbol);
    }

    public static Identifier of(String left) {
        return new Identifier(left);
    }
    public static Identifier id(String left) {
        return new Identifier(left);
    }

    public static List<Identifier> of(String... left) {
        return Arrays.stream(left).map(Identifier::of).toList();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public String symbolWithType() {
        return symbol;
    }

}
