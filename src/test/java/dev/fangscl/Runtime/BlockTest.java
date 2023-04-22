package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class BlockTest extends BaseTest{
    @Test
    void evalLastStatement() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x=10
                var y=20
                x*y+30
                """)));
        var expected = IntegerValue.of(230);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }
    @Test
    void nestedBlock() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    var x=10
                    {
                        var x = 2
                    }
                    x
                }
                """)));
        var expected = IntegerValue.of(10);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }
}
