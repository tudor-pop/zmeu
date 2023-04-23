package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LambdaTest extends BaseTest {

    @Test
    void funDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    fun onClick(callback){
                        var x = 10
                        var y = 20
                        callback(x+y)
                    }
                    onClick((data)->data*10)
                }""")));

        var expected = IntegerValue.of(300);
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

}
