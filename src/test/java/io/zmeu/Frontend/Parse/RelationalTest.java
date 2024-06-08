package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Expressions.LogicalExpression;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.binary;
import static io.zmeu.Frontend.Parser.Factory.program;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class RelationalTest extends BaseTest {

    @Test
    void testGreaterThan() {
        var res = parse("x>2");
        var expected = program(binary("x", 2, ">"));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testGreaterThanEq() {
        var res = parse("x>=2");
        var expected = program(binary("x", 2, ">="));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testLessThan() {
        var res = parse("x<2");
        var expected = program(binary("x", 2, "<"));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testLessThanEq() {
        var res = parse("x<=2");
        var expected = program(binary("x", 2, "<="));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testLessLowerPrecedenceThanAdditive() {
        var res = parse("x+2 > 10");
        var expected = program(BinaryExpression.binary(binary("x", 2, "+"), 10, ">"));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveTrue() {
        var res = parse("x > 2 == true");
        var expected = program(BinaryExpression.binary(binary("x", 2, ">"), true, "=="));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveFalse() {
        var res = parse("x > 2 == false");
        var expected = program(BinaryExpression.binary(binary("x", 2, ">"), false, "=="));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveNotFalse() {
        var res = parse("x > 2 != false");
        var expected = program(BinaryExpression.binary(binary("x", 2, ">"), false, "!="));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLessLowerPrecedenceThanAdditiveNotTrue() {
        var res = parse("x > 2 != true");
        var expected = program(BinaryExpression.binary(binary("x", 2, ">"), true, "!="));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalAnd() {
        var res = parse("x > 0 && y < 0");
        var expected = program(LogicalExpression.of("&&", binary("x", 0, ">"), binary("y", 0, "<")));
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testLogicalOr() {
        var res = parse("x > 0 || y < 0");
        var expected = program(
                LogicalExpression.of("||",
                        binary("x", 0, ">"),
                        binary("y", 0, "<")
                )
        );
        log.info(toJson(res));
        assertEquals(expected, res);
    }

}
