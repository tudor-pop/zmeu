package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.ResourceValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import dev.fangscl.Runtime.Values.SchemaValue;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void resourceAccessSchemaValue() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        assertEquals(IntegerValue.of(2), resource.getEnvironment().lookup("x"));
    }

    @Test
    void resourceMemberAccess() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                var y = Vm.main
                var z = Vm.main.x
                z
                """)));
        log.warn(gson.toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);
        // make sure main has it's own copy of x
        assertNotSame(IntegerValue.of(2), resource.getEnvironment().get("x"));
        // make sure main's x has been changed
        assertEquals(IntegerValue.of(2), resource.getEnvironment().get("x"));

        // assert y holds reference to Vm.main
        var y = global.lookup("y");
        assertSame(y, resource);
        // assert y holds reference to Vm.main
        var z = global.lookup("z");
        assertSame(z, schema.getEnvironment().get("x"));

        assertEquals(IntegerValue.of(2), res);
    }

    /**
     * Test weather changing a value in the instance works
     * It should not change the schema default values
     * It should only change the member of the resource
     */
    @Test
    void resourceSetMemberAccess() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                Vm.main.x = 3
                """)));
        log.warn(gson.toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        // default x in schema remains the same
        assertEquals(IntegerValue.of(2), schema.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(IntegerValue.of(3), res);
        assertEquals(IntegerValue.of(3), x);
    }

    @Test
    void resourceInit() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        // default x in schema remains the same
        assertEquals(IntegerValue.of(2), schema.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(IntegerValue.of(3), x);
    }

    @Test
    void resourceThrows() {
        assertThrows(NotFoundException.class, () -> {
            interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                    schema Vm {
                    }
                                    
                    resource Vm main {
                        x = 3
                    }
                    """)));
        });
    }


}
