package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
import dev.fangscl.Frontend.Parser.NodeType;
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

    public static List<Identifier> of(String... left) {
        return Arrays.stream(left).map(Identifier::of).toList();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

}
