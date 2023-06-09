package dev.fangscl.Runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.ResourceValue;
import dev.fangscl.Runtime.Values.SchemaValue;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class ResourceTest extends BaseTest {
    @Test
    void newResourceThrowsIfNoNameIsSpecified() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                    schema Vm { }
                    resource Vm {
                        
                    }
                    """)));
        });
    }

    /**
     * This checks for the following syntax
     * Vm.main
     * All resources are defined in the schema
     * global env{
     *     Vm : SchemaValue -> variables{ main -> resource Vm}
     * }
     */
    @Test
     void resourceIsDefinedInSchemaEnv() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm { }
                resource Vm main {
                    
                }
                """)));
        log.warn(toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        assertNotNull(schema);
        assertEquals(Identifier.of("Vm"), schema.getName());


        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        assertNotNull(resource);
        assertEquals(Identifier.of(main), resource.getName());
    }

    @Test
    @DisplayName("throw if a resource uses a field not defined in the schema")
    void resourceThrowsIfFieldNotDefinedInSchema() {
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


    @Test
    void resourceInheritsDefaultSchemaValue() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                """)));
        log.warn(toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        assertEquals(IntegerValue.of(2), resource.getEnvironment().lookup("x"));
    }

    @Test
    void resourceMemberAccess() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                var y = Vm.main
                var z = Vm.main.x
                z
                """)));
        log.warn(toJson(res));
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
     * Change a value in the resource instance works
     * It should not change the schema default values
     * It should only change the member of the resource
     */
    @Test
    void resourceSetMemberAccess() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                Vm.main.x = 3
                """)));
        log.warn(toJson(res));
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
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """)));
        log.warn(toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        // default x in schema remains the same
        assertEquals(IntegerValue.of(2), schema.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(IntegerValue.of(3), x);
    }
    @SneakyThrows
    @Test
    void resourceInitJson() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """)));
        log.warn(toJson(res));
        SchemaValue schema = (SchemaValue) global.get("Vm");

        String main = "main";
        ResourceValue resource = (ResourceValue) schema.getEnvironment().get(main);

        var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        log.warn(mapper.writeValueAsString(resource.asData()));

    }


}
