package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
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

    public static RuntimeValue<String> of(String string) {
        return new IdentifierValue(string);
    }
    public static RuntimeValue<String> of(Identifier string) {
        return new IdentifierValue(string);
    }
    public static RuntimeValue<String> of(Expression string) {
        if (string instanceof Identifier s)
            return new IdentifierValue(s);
        throw new RuntimeException("Invalid variable name: " + string.toSExpression());
    }
}
