package dev.fangscl.Runtime;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class FunCoreTest extends BaseTest {

    @Test
    void funDeclaration() {
        var res = (String) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                date()
                """)));

        log.warn(toJson(res));
        assertEquals(LocalDate.now().toString(), res);
    }


}
