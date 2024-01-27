package io.zmeu.Frontend.Parse;

import io.zmeu.ErrorSystem;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ResourceTest extends BaseTest {

    @Test
    void resourceDeclaration() {
        var res = parse("resource vm main {  }");
        var expected = program(resource("vm", "main", block()));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void missingLeftBracketError() {
        parse("resource vm main   }");
        var leftErr = ErrorSystem.getErrors().get(0);
    }

    @Test
    void resourceWithStringAssignment() {
        var res = parse("""
                    resource vm main { 
                        name = "main" 
                    }
                """);
        var expected = program(resource("vm", "main", block(
                assign("name", "main")
        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void resourceWithNumberAssignment() {
        var res = parse("""
                    resource vm main { 
                        name = 1
                    }
                """);
        var expected = program(resource("vm", "main", block(
                assign("name", 1)
        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void resourceWithMemberAssignment() {
        var res = parse("""
                    resource vm main { 
                        name = a.b
                    }
                """);
        var expected = program(resource("vm", "main", block(
                assign("name", member("a", "b"))
        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
