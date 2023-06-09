package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.BooleanValue;
import dev.fangscl.Runtime.Values.DecimalValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class UnaryTest extends BaseTest {

    @Test
    void incrementInt() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    ++x
                }
                """)));
        var expected = IntegerValue.of(2);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void decrementInt() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    --x
                }
                """)));
        var expected = IntegerValue.of(0);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }
    @Test
    void incrementDecimal() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1.1
                    ++x
                }
                """)));
        var expected = DecimalValue.of(2.1);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void decrementDecimal() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1.1
                    --x
                }
                """)));
        var expected = DecimalValue.of(0.1);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }
    @Test
    void unaryMinus() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    -x
                }
                """)));
        var expected = IntegerValue.of(-1);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void unaryMinusDecimal() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1.5
                    -x
                }
                """)));
        var expected = DecimalValue.of(-1.5);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void notFalse() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = false
                    !x 
                }
                """)));
        var expected = BooleanValue.of(true);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void notTrue() {
        Object res = (Object) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = true
                    !x 
                }
                """)));
        var expected = BooleanValue.of(false);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }



}
