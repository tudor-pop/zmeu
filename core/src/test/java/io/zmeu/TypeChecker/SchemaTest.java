package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.SchemaType;
import io.zmeu.TypeChecker.Types.ValueType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("TypeChecker Schema")
public class SchemaTest extends BaseChecker {

    @Test
    void equals() {
        var typeEnv = new TypeEnvironment(Map.of("VERSION", ValueType.String));
        var base = new SchemaType("dog", typeEnv);
        var schema = new SchemaType("cat", typeEnv);
        var schema2 = new SchemaType("cat", typeEnv);

        assertEquals(schema, schema2);
    }

    @Test
    void empty() {
        var actual = checker.visit(src("""
                schema Vm {
                
                }
                """));
        assertEquals(SchemaType.class, actual.getClass());
        assertEquals(new SchemaType("Vm", checker.getEnv()), actual);
    }

    @Test
    void singleProperty() {
        var actual = checker.visit(src("""
                schema Vm {
                   var x  Number
                }
                """));
        assertEquals(SchemaType.class, actual.getClass());

        SchemaType vm = new SchemaType("Vm", checker.getEnv());
        vm.setProperty("x", ValueType.Number);
        assertEquals(vm, actual);
    }

    @Test
    void singlePropertyInit() {
        var actual = checker.visit(src("""
                schema Vm {
                   var x  Number = 1
                }
                """));
        assertEquals(SchemaType.class, actual.getClass());

        SchemaType vm = new SchemaType("Vm", checker.getEnv());
        vm.setProperty("x", ValueType.Number);
        assertEquals(vm, actual);
    }

    @Test
    void singlePropertyInitThrows() {
        Assertions.assertThrows(TypeError.class, () -> checker.visit(src("""
                schema Vm {
                   var x  Number = true
                }
                """)));
    }

    @Test
    void multipleProperty() {
        var actual = checker.visit(src("""
                schema Vm {
                   var x  Number
                   var y  String
                }
                """));
        assertEquals(SchemaType.class, actual.getClass());

        var vm = new SchemaType("Vm", checker.getEnv());
        vm.setProperty("x", ValueType.Number);
        vm.setProperty("y", ValueType.String);
        assertEquals(vm, actual);
    }

    @Test
    void multiplePropertyInit() {
        var actual = checker.visit(src("""
                schema Vm {
                   var x  Number = 2
                   var y  String = "test"
                }
                """));
        assertEquals(SchemaType.class, actual.getClass());

        var vm = new SchemaType("Vm", checker.getEnv());
        vm.setProperty("x", ValueType.Number);
        vm.setProperty("y", ValueType.String);
        assertEquals(vm, actual);
    }


}
