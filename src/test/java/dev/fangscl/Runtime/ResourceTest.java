package dev.fangscl.Runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.ResourceValue;
import dev.fangscl.Runtime.Values.TypeValue;
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
                    type Vm { }
                    resource Vm {
                        
                    }
                    """)));
        });
    }

    /**
     * This checks for the following syntax
     * Vm.main
     * All resources are defined in the type
     * global env{
     * Vm : SchemaValue -> variables{ main -> resource Vm}
     * }
     */
    @Test
    void resourceIsDefinedInSchemaEnv() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm { }
                resource Vm main {
                    
                }
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        assertNotNull(type);
        assertEquals(Identifier.of("Vm"), type.getType());


        var resource = (ResourceValue) type.getEnvironment().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
    }

    @Test
    void resourceIsDefinedInSchema() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm { 
                    var name
                    var maxCount
                }
                resource Vm main {
                    name = "first"
                    maxCount = 2
                }
                resource Vm second {
                    name = "first"
                    maxCount = 1
                }
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        assertNotNull(type);
        assertEquals("Vm", type.getType());


        var resource = (ResourceValue) type.getInstances().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
    }

    @Test
    @DisplayName("throw if a resource uses a field not defined in the type")
    void resourceThrowsIfFieldNotDefinedInSchema() {
        assertThrows(NotFoundException.class, () -> {
            interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                    type Vm {
                    }
                                    
                    resource Vm main {
                        x = 3
                    }
                    """)));
        });
    }


    @Test
    void resourceInheritsDefaultSchemaValue() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        var resource = (ResourceValue) type.getEnvironment().get("main");

        assertEquals(2, resource.getEnvironment().lookup("x"));
    }

    @Test
    void resourceMemberAccess() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                var y = Vm.main
                var z = Vm.main.x
                z
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        var resource = (ResourceValue) type.getEnvironment().get("main");
        assertSame(2, resource.getEnvironment().get("x"));
        // make sure main's x has been changed
        assertEquals(2, resource.getEnvironment().get("x"));

        // assert y holds reference to Vm.main
        var y = global.lookup("y");
        assertSame(y, resource);
        // assert y holds reference to Vm.main
        var z = global.lookup("z");
        assertSame(z, type.getEnvironment().get("x"));

        assertEquals(2, res);
    }

    /**
     * Change a value in the resource instance works
     * It should not change the type default values
     * It should only change the member of the resource
     */
    @Test
    void resourceSetMemberAccess() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                Vm.main.x = 3
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        var resource = (ResourceValue) type.getEnvironment().get("main");

        // default x in type remains the same
        assertEquals(2, type.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(3, res);
        assertEquals(3, x);
    }

    @Test
    void resourceInit() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        var resource = (ResourceValue) type.getEnvironment().get("main");

        // default x in type remains the same
        assertEquals(2, type.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(3, x);
    }

    @SneakyThrows
    @Test
    void resourceInitJson() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """)));
        log.warn(toJson(res));
        var type = (TypeValue) global.get("Vm");

        var resource = (ResourceValue) type.getEnvironment().get("main");

        var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        log.warn(mapper.writeValueAsString(resource.asData()));

    }


}
