package io.zmeu.Frontend.Parser.Literals;

import io.zmeu.Frontend.Parser.Expressions.Visitor;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.api.types.Types;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * A string literal has the form of: "hello" or empty string ""
 * StringLiteral
 * : STRING
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StringLiteral extends Literal {
    private String value;

    public StringLiteral() {
        this.kind = NodeType.StringLiteral;
    }

    public StringLiteral(String value) {
        this();
        setValue(value);
    }

    public static StringLiteral of(String value) {
        return new StringLiteral(value);
    }
    public static StringLiteral string(String value) {
        return new StringLiteral(value);
    }

    private void setValue(String value) {
        if (StringUtils.isBlank(value)) {
            this.value = value;
        } else {
            this.value = Optional.ofNullable(StringUtils.substringBetween(value, "\"", "\""))
                    .or(() -> Optional.ofNullable(StringUtils.substringBetween(value, "'", "'")))
                    .orElse(value);
        }
    }

    public StringLiteral(Object value) {
        this();
        if (value instanceof String s) {
            setValue(s);
        }
    }

    @Override
    public Object getVal() {
        return value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
