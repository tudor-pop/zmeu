package dev.fangscl.Runtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogicalTest extends BaseTest {

    @Test
    void trueOrTrue() {
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("true || true")));
        Assertions.assertTrue(res);
    }

    @Test
    void trueOrFalse() {
        // true || false -> true
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("true || false")));
        Assertions.assertTrue(res);
    }

    @Test
    void falseOrTrue() {
        // false || true -> true
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("false || true")));
        Assertions.assertTrue(res);
    }

    @Test
    void falseOrFalse() {
        // false || false -> true
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("false || false")));
        Assertions.assertFalse(res);
    }

    @Test
    void falseAndTrue() {
        // null && 2 -> null
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("null && 2")));
        Assertions.assertNull(res);
    }

    @Test
    void trueAndFalse() {
        // 2 && null -> null
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("2 && null")));
        Assertions.assertNull(res);
    }

    @Test
    void trueAndTrue() {
        // 1 && 2 -> 2
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("1 && 2")));
        Assertions.assertEquals(2, res);
    }

    @Test
    void falseAndFalse() {
        // false && false -> false
        var res = (Boolean) interpreter.eval(parser.produceAST(tokenizer.tokenize("false && false")));
        Assertions.assertFalse(res);
    }

}
