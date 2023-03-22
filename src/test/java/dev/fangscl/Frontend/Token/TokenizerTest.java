package dev.fangscl.Frontend.Token;

import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Log4j2
public class TokenizerTest {
    private Tokenizer tokenizer;

    @BeforeEach
    void beforeEach() {
        tokenizer = new Tokenizer();
    }


    @Test
    void parseSingleDecimalNumber() throws IOException {
        var token = tokenizer.tokenize("var x=.1");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals(".1", token.get(3).getValue());
    }

    @Test
    void parseDoubleDecimalNumbers() throws IOException {
        var token = tokenizer.tokenize("var x=.12");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals(".12", token.get(3).getValue());
    }

    @Test
    void parseDecimalWithPrefix() throws IOException {
        var token = tokenizer.tokenize("var x=0.12");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals("0.12", token.get(3).getValue());
    }

}
