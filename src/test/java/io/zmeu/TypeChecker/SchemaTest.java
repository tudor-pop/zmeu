package io.zmeu.TypeChecker;

import io.zmeu.TypeChecker.Types.ValueType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("TypeChecker Schema")
public class SchemaTest extends BaseChecker {

    @Test
    void schema() {
        var actual = checker.eval(src("""
                schema Vm {
                
                }
                """));
        assertEquals(ValueType.Number, actual);
    }



}
