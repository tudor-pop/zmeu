package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ForTest extends BaseTest {

    @Test
    void increment() {
        var res = (IntegerValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                 var a = 0
                 var temp
                 for (var b = 1; a < 100; b = temp + b) {
                   temp = a;
                   a = b;
                 }
                """)));
        var expected = IntegerValue.of(233);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void incrementEq() {
        var res = (IntegerValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                 for (var a = 1; a < 10; a=a+1) {
                   a;
                 }
                """)));
        var expected = IntegerValue.of(10);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void incrementTestOnly() {
        var res = (IntegerValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var a = 1
                 for (; a < 10; a=a+1) {
                   a
                 }
                """)));
        var expected = IntegerValue.of(10);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }


}
