package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class VariableDeclarationTest extends BaseTest {


    @Test
    void varNull() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x")));
        assertNull(res);
        assertTrue(global.hasVar("x"));
        assertNull(global.get("x"));
        log.info(toJson(res));
    }

    @Test
    void varInt() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2")));
        var expected = IntegerValue.of(2);
        assertEquals(expected, res);
        log.info(toJson(res));
    }


    @Test
    void varDecimal() {
        var res = (Double) interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2.1")));
        assertEquals(2.1, res);
        log.info(toJson(res));
    }

    @Test
    void varBool() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = true")));
        assertTrue(res);
        log.info(toJson(res));
    }


    @Test
    void varExpressionPlus() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2+2")));
        var expected = IntegerValue.of(4);
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionMinus() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2-2")));
        var expected = IntegerValue.of(0);
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionMultiplication() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2*2")));
        var expected = IntegerValue.of(4);
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionDivision() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2/2")));
        var expected = IntegerValue.of(1);
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionBoolean() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2==2")));
        var expected = true;
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varExpressionBooleanFalse() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2==1")));
        var expected = false;
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void varMultiDeclaration() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var y = 0
                    y=1
                }
                """)));

        log.info(toJson(res));
        var expected = IntegerValue.of(1);
        assertEquals(expected, res);
    }

}
