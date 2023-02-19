package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Base.Expression;
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
public class StringLiteral extends Expression {
    private String value;

    public StringLiteral() {
        this.kind = NodeType.StringLiteral;
    }

    public StringLiteral(String value) {
        this();
        if (StringUtils.isBlank(value)) {
            this.value = value;
        } else {
            this.value = Optional.ofNullable(StringUtils.substringBetween(value, "\"", "\""))
                    .or(() -> Optional.ofNullable(StringUtils.substringBetween(value, "'", "'")))
                    .orElse(value);
        }
    }

}
