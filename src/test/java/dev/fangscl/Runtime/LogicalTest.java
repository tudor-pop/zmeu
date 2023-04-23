package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LogicalTest extends BaseTest {

    @Test
    void consequentLess() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void consequentLessEq() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void consequentGreat() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void consequentGreatEq() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternateGreat() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternateGreatEq() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternateEq() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void alternate() {
        RuntimeValue<IntegerValue> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }


}
