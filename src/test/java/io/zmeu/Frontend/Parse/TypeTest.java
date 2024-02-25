package io.zmeu.Frontend.Parse;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.*;
import static io.zmeu.Frontend.Parser.Literals.StringLiteral.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class TypeTest extends BaseTest {

    @Test
    void testString() {
        var res = parse("var x:String\n");
        var expected = program(var(id("x"), type("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringEOF() {
        var res = parse("var x:String");
        var expected = program(var(id("x"), type("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringLineTerminator() {
        var res = parse("var x:String;");
        var expected = program(var(id("x"), type("String")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testStringInit() {
        var res = parse("""
                var x:String="test"
                """);
        var expected = program(var(id("x"), type("String"), string("test")));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
