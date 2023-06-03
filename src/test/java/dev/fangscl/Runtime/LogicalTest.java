package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LogicalTest extends BaseTest {

    @Test
    void consequentLess() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    var y = 2
                    if (x < y){
                        x = y 
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(2);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void consequentLessEq() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 2
                    var y = 2
                    if (x <= y){
                        x = y 
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(2);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void consequentGreat() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 3
                    var y = 2
                    if (x > y){
                        x = y 
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(2);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void consequentGreatEq() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 2
                    var y = 2
                    if (x >= y){
                        x = 3 
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(3);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternateGreat() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    var y = 2
                    if (x > y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(3);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternateGreatEq() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    var y = 2
                    if (x >= y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(3);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternateEq() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 2
                    var y = 1
                    if (x <= y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(3);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternate() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 2
                    var y = 1
                    if (x < y){
                        x = y 
                    } else {
                        x = 3
                    }
                    x       
                }
                """)));
        var expected = IntegerValue.of(3);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }


}
