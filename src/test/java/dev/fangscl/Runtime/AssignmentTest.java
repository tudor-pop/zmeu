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
    void GlobalVarInt() {
        setGlobalVar(IntegerValue.of(2));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = IntegerValue.of(2);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void GlobalBool() {
        setGlobalVar(BooleanValue.of(false));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = BooleanValue.of(false);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void GlobalBoolTrue() {
        setGlobalVar(BooleanValue.of(true));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = BooleanValue.of(true);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void Decimal() {
        setGlobalVar(DecimalValue.of(1.1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = DecimalValue.of(1.1);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }


    @Test
    void Null() {
        setGlobalVar(new NullValue());

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = new NullValue();
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void AssignmentInt() {
        setGlobalVar(IntegerValue.of(1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=2")));
        var expected = IntegerValue.of(2);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void AssignmentIntSame() {
        setGlobalVar(IntegerValue.of(1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1")));
        var expected = IntegerValue.of(1);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void AssignmentBool() {
        setGlobalVar(BooleanValue.of(true));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=true")));
        var expected = BooleanValue.of(true);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void AssignmentBoolDifferent() {
        setGlobalVar(BooleanValue.of(true));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=false")));
        var expected = BooleanValue.of(false);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void AssignmentDecimalSame() {
        setGlobalVar(DecimalValue.of(1.1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1.1")));
        var expected = DecimalValue.of(1.1);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }

    @Test
    void AssignmentDecimalDifferent() {
        setGlobalVar(DecimalValue.of(1.1));

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1.2")));
        var expected = DecimalValue.of(1.2);
        assertEquals(expected, res);
        log.warn(gson.toJson(res));
    }


}
