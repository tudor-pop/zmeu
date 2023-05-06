package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.ResourceValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import dev.fangscl.Runtime.Values.SchemaValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
public class ResourceTest extends BaseTest {
    @Test
    void resourceNewNoName() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                    schema Vm { }
                    resource Vm {
                        
                    }
                    """)));
        });
    }

    @Test
    void resourceNew() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm { }
                resource Vm main {
                    
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertNotNull(actual);
        assertEquals(Identifier.of("Vm"), actual.getName());


        String main = "main";
        ResourceValue resource = (ResourceValue) actual.getEnvironment().get(main);

        assertNotNull(resource);
        assertEquals(Identifier.of(main), resource.getName());
    }

    @Test
    void resourceNewSchema() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                     
                }
                
                resource Vm main {
                    
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertNotNull(actual);
        assertEquals(Identifier.of("Vm"), actual.getName());


        String main = "main";
        ResourceValue resource = (ResourceValue) global.get(main);

        assertNotNull(resource);
        assertEquals(Identifier.of(main), resource.getName());
    }


}
