package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class AssignmentTest extends BaseTest {

    private void setGlobalVar(RuntimeValue of) {
        environment = new Environment(Map.entry("VERSION", of));
        interpreter.set(environment);
    }

    @Test
    void testGlobalVarInt() {
        setGlobalVar(IntegerValue.of(2));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = IntegerValue.of(2);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testGlobalBool() {
        setGlobalVar(BooleanValue.of(false));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = BooleanValue.of(false);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testGlobalBoolTrue() {
        setGlobalVar(BooleanValue.of(true));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = BooleanValue.of(true);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testDecimal() {
        setGlobalVar(DecimalValue.of(1.1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = DecimalValue.of(1.1);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


    @Test
    void testNull() {
        setGlobalVar(new NullValue());

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = new NullValue();
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentInt() {
        setGlobalVar(IntegerValue.of(1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=2")));
        var expected = IntegerValue.of(2);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentIntSame() {
        setGlobalVar(IntegerValue.of(1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1")));
        var expected = IntegerValue.of(1);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentBool() {
        setGlobalVar(BooleanValue.of(true));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=true")));
        var expected = BooleanValue.of(true);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentBoolDifferent() {
        setGlobalVar(BooleanValue.of(true));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=false")));
        var expected = BooleanValue.of(false);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentDecimalSame() {
        setGlobalVar(DecimalValue.of(1.1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1.1")));
        var expected = DecimalValue.of(1.1);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testAssignmentDecimalDifferent() {
        setGlobalVar(DecimalValue.of(1.1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1.2")));
        var expected = DecimalValue.of(1.2);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
