package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class AssignmentTest extends BaseTest {

    private void setGlobalVar(Object of) {
        global = new Environment(Map.entry("VERSION", of));
        interpreter.set(global);
    }

    @Test
    void GlobalVarInt() {
        setGlobalVar(2);

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        assertEquals(2, res);
        log.warn(toJson(res));
    }

    @Test
    void GlobalBool() {
        setGlobalVar(false);

        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void GlobalBoolTrue() {
        setGlobalVar(true);

        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void Decimal() {
        setGlobalVar(1.1);

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        assertEquals(1.1, res);
        log.warn(toJson(res));
    }


    @Test
    void Null() {
        setGlobalVar(new NullValue());

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION")));
        var expected = new NullValue();
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignmentInt() {
        setGlobalVar(1);

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=2")));
        var expected = 2;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignmentIntSame() {
        setGlobalVar(1);

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1")));
        var expected = 1;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignmentBool() {
        setGlobalVar(true);

        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=true")));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignmentBoolDifferent() {
        setGlobalVar(true);

        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=false")));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignmentDecimalSame() {
        setGlobalVar(1.1);

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1.1")));
        var expected = 1.1;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignmentDecimalDifferent() {
        setGlobalVar(1.1);

        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("VERSION=1.2")));
        var expected = 1.2;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignAddition() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 1.1+2.2
                """)));
        var expected = 1.1 + 2.2;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignMultiplication() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 1.1*2.2
                """)));
        var expected = 1.1 * 2.2;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignDivision() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 2.1/2.2
                """)));
        var expected = 2.1 / 2.2;
        assertEquals(expected, res);
        log.warn(toJson(res));
    }

    @Test
    void AssignBooleanFalse() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 1==2
                """)));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignBooleanTrue() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 1==1
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignLess() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 3 < 2
                """)));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignLessTrue() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 3 < 3.1
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignLessFalse() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 3.2 < 3.1
                """)));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignGreater() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 3 > 2
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignGreaterEq() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 3 >= 2
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignGreaterEqTrue() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 2 >= 2
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignGreaterEqFalse() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 1 >= 2
                """)));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignLessEq() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 3 <= 2
                """)));
        assertFalse(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignLessEqTrue() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 2 <= 2
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }

    @Test
    void AssignLessEqFalse() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=0
                x = 1 <= 2
                """)));
        assertTrue(res);
        log.warn(toJson(res));
    }


}
