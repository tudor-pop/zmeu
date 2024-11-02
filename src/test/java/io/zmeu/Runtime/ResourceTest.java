package io.zmeu.Runtime;

import io.zmeu.Runtime.Values.ResourceValue;
import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.Runtime.exceptions.NotFoundException;
import io.zmeu.Runtime.exceptions.RuntimeError;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class ResourceTest extends BaseRuntimeTest {
    @Test
    void newResourceThrowsIfNoNameIsSpecified() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            eval("""
                    schema vm { }
                    resource vm {
                    
                    }
                    """);
        });
    }

    /**
     * This checks for the following syntax
     * vm.main
     * All resources are defined in the schema
     * global env{
     * vm : SchemaValue -> variables{ main -> resource vm}
     * }
     */
    @Test
    void resourceIsDefinedInSchemaEnv() {
        var res = eval("""
                schema vm { }
                resource vm main {
                
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        assertNotNull(schema);
        assertEquals("vm", schema.getType().string());


        var resource = (ResourceValue) schema.getInstances().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
    }

    @Test
    void resourceIsDefinedInSchema() {
        var res = eval("""
                schema vm { 
                    var name
                    var maxCount=0
                }
                resource vm main {
                    name = "first"
                    maxCount=1
                }
                resource vm second {
                    name = "second"
                    maxCount = vm.main.maxCount
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        assertNotNull(schema);
        assertEquals("vm", schema.getType().string());


        var resource = (ResourceValue) schema.getInstances().get("main");

        assertNotNull(resource);
        assertEquals("main", resource.getName());
        assertEquals("first", resource.argVal("name"));
        assertEquals(1, resource.argVal("maxCount"));

        var second = (ResourceValue) schema.getInstances().get("second");

        assertNotNull(second);
        assertEquals("second", second.getName());
        assertEquals("second", second.argVal("name"));
        assertEquals(1, second.argVal("maxCount"));
    }

    @Test
    @DisplayName("Evaluate dependency")
    void resourceIsDefinedInSchemaDependencyFirst() {
        var res = eval("""
                schema vm { 
                    var name:String
                    var maxCount=0
                    var minCount=0
                }
                resource vm second {
                    name = "second"
                    maxCount = vm.main.maxCount
                    minCount = vm.main.minCount
                }
                resource vm main {
                    name = "main"
                    maxCount = 2
                    minCount = 1
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        assertNotNull(schema);
        assertEquals("vm", schema.getType().string());


        var resource = (ResourceValue) schema.getInstances().get("main");
        assertNotNull(resource);
        assertEquals("main", resource.getName());
        assertEquals("main", resource.argVal("name"));
        assertEquals(2, resource.argVal("maxCount"));
        assertEquals(1, resource.argVal("minCount"));

        var second = (ResourceValue) schema.getInstances().get("second");
        assertNotNull(second);
        assertEquals("second", second.getName());
        assertEquals("second", second.argVal("name"));
        assertEquals(2, second.argVal("maxCount"));
        assertEquals(1, second.argVal("minCount"));
    }

    @Test
    @DisplayName("Evaluate dependency and pull value from global schema")
    void evalDependencyFirstMissingProperty() {
        var res = eval("""
                schema vm { 
                    var name:String
                    var maxCount=0
                    var minCount=1
                }
                resource vm second {
                    name = "second"
                    maxCount = vm.main.maxCount
                    minCount = vm.main.minCount
                }
                resource vm main {
                    name = "main"
                    maxCount = 2
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        assertNotNull(schema);
        assertEquals("vm", schema.getType().string());


        var resource = (ResourceValue) schema.getInstances().get("main");
        assertNotNull(resource);
        assertEquals("main", resource.getName());
        assertEquals("main", resource.argVal("name"));
        assertEquals(2, resource.argVal("maxCount"));
        assertEquals(1, resource.argVal("minCount"));

        var second = (ResourceValue) schema.getInstances().get("second");
        assertNotNull(second);
        assertEquals("second", second.getName());
        assertEquals("second", second.argVal("name"));
        assertEquals(2, second.argVal("maxCount"));
        assertEquals(1, second.argVal("minCount"));
    }

    @Test
    @DisplayName("throw if a resource uses a field not defined in the schema")
    void resourceThrowsIfFieldNotDefinedInSchema() {
        assertThrows(NotFoundException.class, () -> eval("""
                schema vm {
                }
                
                resource vm main {
                    x = 3
                }
                """));
    }


    @Test
    void resourceInheritsDefaultSchemaValue() {
        var res = eval("""
                schema vm {
                   var x = 2
                }
                
                resource vm main {
                
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        var resource = (ResourceValue) schema.getInstances().get("main");

        Assertions.assertEquals(2, resource.getProperties().lookup("x"));
    }

    @Test
    void resourceMemberAccess() {
        var res = eval("""
                schema vm {
                   var x = 2
                }
                
                resource vm main {
                
                }
                var y = vm.main
                var z = vm.main.x
                z
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        var resource = (ResourceValue) schema.getInstances().get("main");
        assertSame(2, resource.getProperties().get("x"));
        // make sure main's x has been changed
        Assertions.assertEquals(2, resource.getProperties().get("x"));

        // assert y holds reference to vm.main
        var y = global.lookup("y");
        assertSame(y, resource);
        // assert y holds reference to vm.main
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
                schema vm {
                   var x = 2
                }
                
                resource vm main {
                
                }
                vm.main.x = 3
                """));
    }

    @Test
    void resourceInit() {
        var res = eval("""
                schema vm {
                   var x = 2
                }
                
                resource vm main {
                    x = 3
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        var resource = (ResourceValue) schema.getInstances().get("main");

        // default x in schema remains the same
        Assertions.assertEquals(2, schema.getEnvironment().get("x"));

        // x of main resource was updated with a new value
        var x = resource.get("x");
        assertEquals(3, x);
    }

    @SneakyThrows
    @Test
    void resourceInitJson() {
        var res = eval("""
                schema vm {
                   var x = 2
                }
                
                resource vm main {
                    x = 3
                }
                """);
        log.warn(toJson(res));
        var schema = (SchemaValue) global.get("vm");

        var resource = schema.getInstances().get("main");

        Assertions.assertInstanceOf(ResourceValue.class, resource);
    }


}
