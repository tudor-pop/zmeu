package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdentifierValue extends RuntimeValue<String> {
    private String value;

    public IdentifierValue(String value) {
        this.value = value;
        this.type = ValueType.Identifier;
    }

    public IdentifierValue(Identifier e) {
        this(e.getSymbol());
    }

    @Override
    public String getRuntimeValue() {
        return value;
    }
}
