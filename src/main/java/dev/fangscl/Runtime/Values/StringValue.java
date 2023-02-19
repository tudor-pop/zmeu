package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Receiving a StringLiteral
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StringValue extends RuntimeValue {
    private String value;

    public StringValue(String value) {
//        if (StringUtils.isBlank(value)) {
//            this.value = value;
//        } else {
//            this.value = Optional.ofNullable(StringUtils.substringBetween(value, "\"", "\""))
//                    .orElse(value);
//        }
        this.type = ValueType.String;
        this.value = value;
    }

    public StringValue(StringLiteral literal) {
        this(literal.getValue());
    }

}
