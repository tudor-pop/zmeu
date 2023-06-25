package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.ResourceValue;
import dev.fangscl.Runtime.Values.SchemaValue;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import dev.fangscl.Runtime.exceptions.RuntimeError;
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
            eval("""
                    schema Vm { }
                    resource Vm {
                        
                    }
                    """);
        });
    }

    /**
     * This checks for the following syntax
     * Vm.main
     * All resources are defined in the schema
     * global env{
     * Vm : SchemaValue -> variables{ main -> resource Vm}
     * }
     */
    @Test
    void resourceIsDefinedInSchemaEnv() {
        var res = eval("""
                schema Vm { }
                resource Vm main {
                    
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("Vm");

        assertNotNull(schema);
        assertEquals("Vm", schema.getType().getSymbol());


        var resource = (ResourceValue) schema.getInstances().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
    }

    @Test
    void resourceIsDefinedInSchema() {
        var res = eval("""
                schema Vm { 
                    var name
                    var maxCount=0
                }
                resource Vm main {
                    name = "first"
                    maxCount=1
                }
                resource Vm second {
                    name = "second"
                    maxCount = Vm.main.maxCount
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("Vm");

        assertNotNull(schema);
        assertEquals("Vm", schema.getType().getSymbol());


        var resource = (ResourceValue) schema.getInstances().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
    }

    @Test
    @DisplayName("throw if a resource uses a field not defined in the schema")
    void resourceThrowsIfFieldNotDefinedInSchema() {
        assertThrows(NotFoundException.class, () -> eval("""
                schema Vm {
                }
                                
                resource Vm main {
                    x = 3
                }
                """));
    }


    @Test
    void resourceInheritsDefaultSchemaValue() {
        var res = eval("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("Vm");

        var resource = (ResourceValue) schema.getInstances().get("main");

        assertEquals(2, resource.getParent().lookup("x"));
    }

    @Test
    void resourceMemberAccess() {
        var res = eval("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                var y = Vm.main
                var z = Vm.main.x
                z
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("Vm");

        var resource = (ResourceValue) schema.getInstances().get("main");
        assertSame(2, resource.getParent().get("x"));
        // make sure main's x has been changed
        assertEquals(2, resource.getParent().get("x"));

        // assert y holds reference to Vm.main
        var y = global.lookup("y");
        assertSame(y, resource);
        // assert y holds reference to Vm.main
        var z = global.lookup("z");
        assertSame(z, schema.getEnvironment().get("x"));

        assertEquals(2, res);
    }

    /**
     * Change a value in the resource instance works
     * It should not change the schema default values
     * It should only change the member of the resource
     */
    @Test
    void resourceSetMemberAccess() {
        Assertions.assertThrows(RuntimeError.class, () -> eval("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    
                }
                Vm.main.x = 3
                """));
    }

    @Test
    void resourceInit() {
        var res = eval("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("Vm");

        var resource = (ResourceValue) schema.getInstances().get("main");

        // default x in schema remains the same
        assertEquals(2, schema.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(3, x);
    }

    @SneakyThrows
    @Test
    void resourceInitJson() {
        var res = eval("""
                schema Vm {
                   var x = 2
                }
                                
                resource Vm main {
                    x = 3
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("Vm");

        var resource = schema.getInstances().get("main");

        Assertions.assertInstanceOf(ResourceValue.class, resource);
    }


}
