package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.Parser.Parser;
import io.zmeu.Frontend.visitors.Visitor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.StringJoiner;

public sealed abstract class Type extends Expression
        permits FunType, ReferenceType, ValueType {
    @Getter
    @Setter
    private String value;

    public Type(String value) {
        this();
        this.value = value;
    }

    public Type() {
        this.kind = NodeType.Type;
    }

    public static Type of(String string) {
        var res = ValueType.of(string);
        if (res == null) {
            return ReferenceType.of(string);
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Type type)) return false;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(getValue(), type.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(getValue()).toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Type.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .add("kind=" + kind)
                .toString();
    }


    public static Type fromString(String symbol) {
        return switch (symbol) {
            case "String" -> ValueType.String;
            case "Number" -> ValueType.Number;
            case "Boolean" -> ValueType.Boolean;
            case "Null" -> ValueType.Null;
            default -> {
                if (symbol.startsWith("(")) {
                    FunType fun = FunStore.getFun(symbol);
                    if (fun != null) {
                        yield fun;
                    } else {
                        FunStore.setFun(symbol, Parser.valueOf(symbol));
                        yield FunStore.getFun(symbol);
                    }
                }
                throw new IllegalArgumentException("Invalid symbol: " + symbol);
            }
        };
    }

    public boolean hasValue() {
        return StringUtils.isNotBlank(value);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
