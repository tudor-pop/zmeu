package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class WhileTest extends BaseTest {

    @Test
    void increment() {
        var res = (IntegerValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    while (x < 5){
                        x = x+1
                    }      
                    x 
                }
                """)));
        var expected = IntegerValue.of(5);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void incrementEq() {
        IntegerValue res = (IntegerValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x = 1
                    while (x <= 5){
                        x = x+1
                    }      
                    x 
                }
                """)));
        var expected = IntegerValue.of(6);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }



}