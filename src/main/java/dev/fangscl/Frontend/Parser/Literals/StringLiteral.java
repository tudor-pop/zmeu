package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.NodeType;
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
    public String toSExpression() {
        return value;
    }

    @Override
    public Object getVal() {
        return value;
    }
}
