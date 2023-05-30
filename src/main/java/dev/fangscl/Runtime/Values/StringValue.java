package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Receiving a StringLiteral
 */
@Data
public class StringValue implements RuntimeValue<String> {
    private String value;

    public StringValue(String value) {
        if (StringUtils.isBlank(value)) {
            this.value = value;
        } else {
            this.value = Optional.ofNullable(StringUtils.substringBetween(value, "\"", "\""))
                    .orElse(value);
        }
//        this.value = value;
    }

    public StringValue(StringLiteral literal) {
        this(literal.getValue());
    }

    public static RuntimeValue of(Expression statement) {
        if (statement instanceof StringLiteral s)
            return new StringValue(s.getValue());
        throw new IllegalStateException();
    }

    public static RuntimeValue of(String statement) {
        return new StringValue(statement);
    }


    @Override
    public String getRuntimeValue() {
        return value;
    }
}
