package dev.fangscl.Runtime.Values;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.BlockStatement;
import dev.fangscl.Runtime.Environment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SchemaValue extends RuntimeValue<Identifier> {
    private Environment environment;
    private Identifier name;
    private BlockStatement body;

    private SchemaValue(Identifier name, BlockStatement body, Environment environment) {
        this.name = name;
        this.body = body;
        this.environment = environment;
    }

    @Override
    public Identifier getRuntimeValue() {
        return name;
    }

    public static SchemaValue of(Identifier name, BlockStatement body, Environment environment) {
        return new SchemaValue(name, body, environment);
    }

    public String getNameString() {
        return name.getSymbol();
    }


}
