package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class BlockTest extends BaseTest {

    @Test
    void testInteger() {
        var res = parse("{ 42 }");
        var expected = Program.of(ExpressionStatement.of(BlockExpression.of(ExpressionStatement.of(42))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testString() {
        var res = parse("{ \"hello\" }");
        var expected = Program.of(ExpressionStatement.of(BlockExpression.of(ExpressionStatement.of("hello"))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }
    @Test
    void testEmptyBlock() {
        var res = parse("{ }    ");
        var expected = Program.of(ExpressionStatement.of(BlockExpression.of(Collections.emptyList())));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


    @Test
    void testNestedBlocksString() {
        var res = parse("{ { \"hello\" } }");
        var expected = Program.of(ExpressionStatement.of(BlockExpression.of(BlockExpression.of(ExpressionStatement.of("hello")))));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


    @Test
    void testEmptyStatement() {
        var res = parse("\n");
        var expected = Program.of();
        assertEquals(expected, res);
        log.info(toJson(res));
    }

    @Test
    void testEmptyStatementInBlock() {
        var res = parse("{ \n }");
        var expected = Program.of(ExpressionStatement.of(BlockExpression.of(Collections.emptyList())));
        assertEquals(expected, res);
        log.info(toJson(res));
    }


}
