package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Runtime.TypeSystem.Base.BlockStatement;
import dev.fangscl.Runtime.TypeSystem.Base.ExpressionStatement;
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

    @Test
    void testDecimal() {
        var res = parser.produceAST(tokenizer.tokenize("1.11"));
        var expected = Program.of(ExpressionStatement.of(Literal.of(1.11)));
        assertEquals(expected, res);
    }

    @Test
    void testStringStatements() {
        var res = parser.produceAST(tokenizer.tokenize("""
                "Hello"
                """));
        var expected = Program.of(
                ExpressionStatement.of("Hello")
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testMultipleStatements() {
        var res = parser.produceAST(tokenizer.tokenize("""
                "Hello"
                1
                """));
        var expected = Program.of(
                ExpressionStatement.of("Hello"),
                ExpressionStatement.of(1)
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
