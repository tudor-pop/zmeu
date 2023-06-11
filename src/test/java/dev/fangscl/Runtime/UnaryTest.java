package dev.fangscl.Runtime;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class UnaryTest extends BaseTest {

    @Test
    void incrementInt() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    ++x
                }
                """)));
        log.warn(toJson(res));
        assertEquals(2, res);
    }

    @Test
    void decrementInt() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    --x
                }
                """)));
        log.warn(toJson(res));
        assertEquals(0, res);
    }

    @Test
    void incrementDecimal() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1.1
                    ++x
                }
                """)));
        log.warn(toJson(res));
        assertEquals(2.1, res);
    }

    @Test
    void decrementDecimal() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1.1
                    --x
                }
                """)));
        log.warn(toJson(res));
        assertEquals(0.1, res);
    }

    @Test
    void unaryMinus() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    -x
                }
                """)));
        log.warn(toJson(res));
        assertEquals(-1, res);
    }

    @Test
    void unaryMinusDecimal() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1.5
                    -x
                }
                """)));
        log.warn(toJson(res));
        assertEquals(-1.5, res);
    }

    @Test
    void notFalse() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = false
                    !x 
                }
                """)));
        log.warn(toJson(res));
        assertTrue(res);
    }

    @Test
    void notTrue() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = true
                    !x 
                }
                """)));
        log.warn(toJson(res));
        assertFalse(res);
    }


}
