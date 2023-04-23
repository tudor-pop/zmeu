package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class BlockTest extends BaseTest {

    @Test
    void testInteger() {
        var res = parser.produceAST(tokenizer.tokenize("{ 42 }"));
        var expected = Program.of(BlockExpression.of(ExpressionStatement.of(42)));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testString() {
        var res = parser.produceAST(tokenizer.tokenize("{ \"hello\" }"));
        var expected = Program.of(BlockExpression.of(ExpressionStatement.of("hello")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }
    @Test
    void testEmptyBlock() {
        var res = parser.produceAST(tokenizer.tokenize("{ }    "));
        var expected = Program.of(BlockExpression.of(Collections.emptyList()));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


    @Test
    void testNestedBlocksString() {
        var res = parser.produceAST(tokenizer.tokenize("{ { \"hello\" } }"));
        var expected = Program.of(BlockExpression.of(BlockExpression.of(ExpressionStatement.of("hello"))));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


    @Test
    void testEmptyStatement() {
        var res = parser.produceAST(tokenizer.tokenize("\n"));
        var expected = Program.of();
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testEmptyStatementInBlock() {
        var res = parser.produceAST(tokenizer.tokenize("{ \n }"));
        var expected = Program.of(BlockExpression.of(Collections.emptyList()));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
