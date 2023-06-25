package dev.fangscl.Frontend.Parse;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static dev.fangscl.Frontend.Parser.Factory.*;
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
    void resourceDeclarationWithProperties() {
        var res = parse("""
                    resource vm main { 
                        name = "main" 
                    }
                """);
        var expected = program(resource("vm", "main", block(
                assign("name","main")
        )));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
