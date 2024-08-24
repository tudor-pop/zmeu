package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Types.ValueType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@DisplayName("TypeChecker Assignment")
public class AssignmentTest extends BaseChecker {

    @Test
    void testSimpleAssignment() {
        var actual = checker.eval(src("""
                var x = 10
                x = 1
                """));
        assertEquals(ValueType.Number, actual);
    }

    @Test
    void testWrongAssignment() {
        assertThrows(TypeError.class, () -> checker.eval(src("""
                // init with number type
                var x = 10 
                // try to change to boolean should throw
                x = false 
                """)));
    }

    @Test
    void testStringInitWrongTypeInt() {
        assertThrows(TypeError.class, () -> checker.eval(src("""
                var x:String=1
                """))
        );
    }
//
//    @Test
//    void testStringInitWrongType() {
//        var actual = parse("""
//                var x:Number="test"
//                """);
//        var errors = ErrorSystem.getErrors();
//        log.info(actual);
//        log.info(ErrorSystem.errors());
//        Assertions.assertFalse(errors.isEmpty());
//    }


}
