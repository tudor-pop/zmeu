package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.BooleanValue;
import dev.fangscl.Runtime.Values.DecimalValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class VariableDeclarationTest extends BaseTest {


    @Test
    void varInt() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2")));
        var expected = IntegerValue.of(2);
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void varDecimal() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = 2.1")));
        var expected = DecimalValue.of(2.1);
        assertEquals(expected.getRuntimeValue(), res.getRuntimeValue());
        log.info(gson.toJson(res));
    }

    @Test
    void varBool() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("var x = true")));
        var expected = BooleanValue.of(true);
        assertEquals(expected.getRuntimeValue(), res.getRuntimeValue());
        log.info(gson.toJson(res));
    }


}
