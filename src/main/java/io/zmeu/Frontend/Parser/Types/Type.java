package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
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

    public static Type fromString(String symbol) {
        return switch (symbol) {
            case "String" -> ValueType.String;
            case "Number" -> ValueType.Number;
            case "Boolean" -> ValueType.Boolean;
            case "Null" -> ValueType.Null;
            default -> {
                if (symbol.startsWith("fun")) {
                    FunType fun = FunStore.getFun(symbol);
                    if (fun != null) {
                        yield fun;
                    } else {
                        FunStore.setFun(symbol, FunType.valueOf(symbol));
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
