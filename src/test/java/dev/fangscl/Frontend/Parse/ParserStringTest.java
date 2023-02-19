package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class ParserStringTest extends ParserStatementTest {

    @Test
    void testHello() {
        var res = parser.produceAST(""" 
                "Hello" 
                """);
        Statement expression = res.first();
        assertEquals(NodeType.StringLiteral, expression.getKind());
        assertEquals("Hello", ((StringLiteral) expression).getValue());
        log.info(gson.toJson(res));
    }

    @Test
    void testIntegerStrShouldEvalToString() {
        var res = parser.produceAST(""" 
                "42" 
                """);
        Statement expression = res.first();
        assertEquals(NodeType.StringLiteral, expression.getKind());
        assertEquals("42", ((StringLiteral) expression).getValue());
        assertEquals("""
                        {"body":[{"value":"42","kind":"StringLiteral"}],"kind":"Program"}"""
                , gson.toJson(res));
        log.info(gson.toJson(res));
    }

    @Test
    void testSingleQuotesShouldEvalToString() {
        var res = parser.produceAST(""" 
                '42' """);
        Statement expression = res.first();
        assertEquals(NodeType.StringLiteral, expression.getKind());
        assertEquals("42", ((StringLiteral) expression).getValue());
        log.info(gson.toJson(res));
    }

    @Test
    void testNumberStringShouldEvalToNumber() {
        var res = parser.produceAST("42");
        Statement expression = res.first();
        assertEquals(NodeType.IntegerLiteral, expression.getKind());
        assertEquals(42, ((NumericLiteral) expression).getValue());
        log.info(gson.toJson(res));
    }

    @Test
    void testNumberStringShouldEvalToNumberWithTrailingSpace() {
        var res = parser.produceAST("   \"  42  \"    ");
        Statement expression = res.first();
        assertEquals(NodeType.StringLiteral, expression.getKind());
        assertEquals("  42  ", ((StringLiteral) expression).getValue());
        log.info(gson.toJson(res));
    }


}
