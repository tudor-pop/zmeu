package dev.fangscl.Frontend.Parse;

import dev.fangscl.Runtime.TypeSystem.Base.BlockStatement;
import dev.fangscl.Runtime.TypeSystem.Program;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ParserBlockTest extends ParserStatementTest {

    @Test
    void testInteger() {
        var res = parser.produceAST(tokenizer.tokenize("{ 42; }"));
        var expected = Program.of(BlockStatement.of("{ 42; }"));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }



}
