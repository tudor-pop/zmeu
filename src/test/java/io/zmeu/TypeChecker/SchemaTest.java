package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.SchemaType;
import io.zmeu.TypeChecker.Types.ValueType;
import lombok.extern.log4j.Log4j2;
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
        var schema = new SchemaType("cat", base, typeEnv);
        var schema2 = new SchemaType("cat", base, typeEnv);

        assertEquals(schema, schema2);
    }

    @Test
    void schema() {
        var actual = checker.eval(src("""
                schema Vm {
                
                }
                """));
        assertEquals(ValueType.Number, actual);
    }


}
