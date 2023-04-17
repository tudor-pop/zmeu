package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class RelationalTest extends StatementTest {

    @Test
    void testGreaterThan() {
        var res = parser.produceAST(tokenizer.tokenize("x>2"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(2), ">")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testGreaterThanEq() {
        var res = parser.produceAST(tokenizer.tokenize("x>=2"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(2), ">=")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testLessThan() {
        var res = parser.produceAST(tokenizer.tokenize("x<2"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(2), "<")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testLessThanEq() {
        var res = parser.produceAST(tokenizer.tokenize("x<=2"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(2), "<=")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testLessLowerPrecedenceThanAdditive() {
        var res = parser.produceAST(tokenizer.tokenize("x+2 > 10"));
        var expected = Program.of(ExpressionStatement.of(
                BinaryExpression.of(
                        BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(2), "+"), NumericLiteral.of(10),
                        ">")
        ));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

}
