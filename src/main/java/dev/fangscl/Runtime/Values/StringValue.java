package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.Statements.Statement;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Receiving a StringLiteral
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StringValue extends RuntimeValue<String> {
    private String value;

    public StringValue(String value) {
//        if (StringUtils.isBlank(value)) {
//            this.value = value;
//        } else {
//            this.value = Optional.ofNullable(StringUtils.substringBetween(value, "\"", "\""))
//                    .orElse(value);
//        }
        this.value = value;
    }

    public StringValue(StringLiteral literal) {
        this(literal.getValue());
    }

    public static RuntimeValue of(Statement statement) {
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
