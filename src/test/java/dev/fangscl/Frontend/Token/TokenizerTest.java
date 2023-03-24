package dev.fangscl.Frontend.Token;

import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
public class TokenizerTest {
    private Tokenizer tokenizer;

    @BeforeEach
    void beforeEach() {
        tokenizer = new Tokenizer();
    }

    @Test
    void testLiteralOneNumber() {
        var result = tokenizer.tokenize("1");
        Assertions.assertEquals(TokenType.Number, result.get(0).getType());
        Assertions.assertEquals(1, result.get(0).getValueInt());
        log.info(result);
    }

    @Test
    void testLiteralMultipleNumbers() {
        var result = tokenizer.tokenize("422");
        Assertions.assertEquals(TokenType.Number, result.get(0).getType());
        Assertions.assertEquals(422, result.get(0).getValueInt());
        log.info(result);
    }

    @Test
    void testLiteralStringNumber() {
        var result = tokenizer.tokenize("""
                "422"
                """);
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\"422\"", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testLiteralString() {
        var result = tokenizer.tokenize("\"hello\"");
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\"hello\"", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testLiteralSingleQuoteString() {
        var result = tokenizer.tokenize("'hello'");
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\'hello\'", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testLiteralWhitespaceString() {
        var result = tokenizer.tokenize("   42    ");
        Assertions.assertEquals(TokenType.Number, result.get(0).getType());
        Assertions.assertEquals("42", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testLiteralWhitespaceStringInside() {
        var result = tokenizer.tokenize("   \"  42  \"    ");
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\"  42  \"", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testOpenParanthesis() {
        var result = tokenizer.tokenize("(");
        Assertions.assertEquals(TokenType.OpenParanthesis, result.get(0).getType());
        Assertions.assertEquals("(", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testCloseParanthesis() {
        var result = tokenizer.tokenize(")");
        Assertions.assertEquals(TokenType.CloseParanthesis, result.get(0).getType());
        Assertions.assertEquals(")", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testOpenBraces() {
        var result = tokenizer.tokenize("{");
        Assertions.assertEquals(TokenType.OpenBraces, result.get(0).getType());
        Assertions.assertEquals("{", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testCloseBraces() {
        var result = tokenizer.tokenize("}");
        Assertions.assertEquals(TokenType.CloseBraces, result.get(0).getType());
        Assertions.assertEquals("}", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testOpenBrackets() {
        var result = tokenizer.tokenize("[");
        Assertions.assertEquals(TokenType.OpenBrackets, result.get(0).getType());
        Assertions.assertEquals("[", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testCloseBrackets() {
        var result = tokenizer.tokenize("]");
        Assertions.assertEquals(TokenType.CloseBrackets, result.get(0).getType());
        Assertions.assertEquals("]", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testNotEquals() {
        var result = tokenizer.tokenize("!=");
        Assertions.assertEquals(TokenType.Bang_Equal, result.get(0).getType());
        Assertions.assertEquals("!=", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testEqualsEquals() {
        var result = tokenizer.tokenize("==");
        Assertions.assertEquals(TokenType.Equal_Equal, result.get(0).getType());
        Assertions.assertEquals("==", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testLessEquals() {
        var result = tokenizer.tokenize("<=");
        Assertions.assertEquals(TokenType.Less_Equal, result.get(0).getType());
        Assertions.assertEquals("<=", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testLess() {
        var result = tokenizer.tokenize("<");
        Assertions.assertEquals(TokenType.Less, result.get(0).getType());
        Assertions.assertEquals("<", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testGreater() {
        var result = tokenizer.tokenize(">");
        Assertions.assertEquals(TokenType.Greater, result.get(0).getType());
        Assertions.assertEquals(">", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testGreaterEquals() {
        var result = tokenizer.tokenize(">=");
        Assertions.assertEquals(TokenType.Greater_Equal, result.get(0).getType());
        Assertions.assertEquals(">=", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testDivision() {
        var result = tokenizer.tokenize("/");
        Assertions.assertEquals(TokenType.Division, result.get(0).getType());
        Assertions.assertEquals("/", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testComment() {
        var result = tokenizer.tokenize("// a comment goes until the end of line \n");
        Assertions.assertEquals("EOF", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testAfterComment() {
        var result = tokenizer.tokenize("// a comment goes until the end of line \n 10");
        Assertions.assertEquals(TokenType.Number, result.get(0).getType());
        Assertions.assertEquals("10", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testUnknownIdentifier() {
        var result = tokenizer.tokenize("tudor");
        Assertions.assertEquals(TokenType.Identifier, result.get(0).getType());
        Assertions.assertEquals("tudor", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testKeywordVar() {
        var result = tokenizer.tokenize("var");
        Assertions.assertEquals(TokenType.Var, result.get(0).getType());
        Assertions.assertEquals("var", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testKeywordModule() {
        var result = tokenizer.tokenize("module");
        Assertions.assertEquals(TokenType.Module, result.get(0).getType());
        Assertions.assertEquals("module", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testKeywordParam() {
        var result = tokenizer.tokenize("param");
        Assertions.assertEquals(TokenType.Param, result.get(0).getType());
        Assertions.assertEquals("param", result.get(0).getValue());
        log.info(result);
    }

    @Test
    void testUnexpected() {
        var result = tokenizer.tokenize("&");
        log.info(result);
    }

}
