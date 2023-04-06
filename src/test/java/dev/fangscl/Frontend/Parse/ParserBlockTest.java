package dev.fangscl.Frontend.Parse;

import dev.fangscl.Runtime.TypeSystem.Statements.BlockStatement;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ParserBlockTest extends ParserStatementTest {

    @Test
    void testInteger() {
        var res = parser.produceAST(tokenizer.tokenize("{ 42 }"));
        var expected = Program.of(BlockStatement.of(ExpressionStatement.of(42)));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testString() {
        var res = parser.produceAST(tokenizer.tokenize("{ \"hello\" }"));
        var expected = Program.of(BlockStatement.of(ExpressionStatement.of("hello")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }
    @Test
    void testEmptyBlock() {
        var res = parser.produceAST(tokenizer.tokenize("{ }    "));
        var expected = Program.of(BlockStatement.of());
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


    @Test
    void testNestedBlocksString() {
        var res = parser.produceAST(tokenizer.tokenize("{ { \"hello\" } }"));
        var expected = Program.of(BlockStatement.of(BlockStatement.of(ExpressionStatement.of("hello"))));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }



}
