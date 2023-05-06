package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.RuntimeValue;
import dev.fangscl.Runtime.Values.SchemaValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ResourceTest extends BaseTest {

    @Test
    void schemaDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm { }
                resource Vm {
                    
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(Identifier.of("Vm"), actual.getName());
        assertEquals(Identifier.of("Vm"), actual.getEnvironment().get("Vm"));
    }


}
