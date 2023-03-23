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
        Assertions.assertEquals(TokenType.Integer, result.get(0).getType());
        Assertions.assertEquals(1, result.get(0).getValueInt());
    }

    @Test
    void testLiteralMultipleNumbers() {
        var result = tokenizer.tokenize("422");
        Assertions.assertEquals(TokenType.Integer, result.get(0).getType());
        Assertions.assertEquals(422, result.get(0).getValueInt());
    }

    @Test
    void testLiteralStringNumber() {
        var result = tokenizer.tokenize("""
                "422"
                """);
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\"422\"", result.get(0).getValue());
    }

    @Test
    void testLiteralString() {
        var result = tokenizer.tokenize("\"hello\"");
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\"hello\"", result.get(0).getValue());
    }

    @Test
    void testLiteralSingleQuoteString() {
        var result = tokenizer.tokenize("'hello'");
        Assertions.assertEquals(TokenType.String, result.get(0).getType());
        Assertions.assertEquals("\'hello\'", result.get(0).getValue());
    }
    @Test
    void testLiteralWhitespaceString() {
        var result = tokenizer.tokenize("   42    ");
        Assertions.assertEquals(TokenType.Integer, result.get(0).getType());
        Assertions.assertEquals("42", result.get(0).getValue());
    }

}
