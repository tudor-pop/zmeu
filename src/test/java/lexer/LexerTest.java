package lexer;

import dev.fangscl.Parsing.Lexer.Lexer;
import dev.fangscl.Parsing.Lexer.LexerTokenException;
import dev.fangscl.Parsing.Lexer.TokenType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class LexerTest {
    private Lexer lexer;

    @BeforeEach
    void beforeEach() {
        lexer = new Lexer();
    }

    @Test
    void checkLoop() throws IOException {
        var lines = Files.readAllLines(Paths.get("test-1.fcl"));
        for (var line : lines) {
            lexer.tokenize(line);
        }
        log.debug("lines: {}", lexer.getTokens());
    }

    @Test
    void parseDouble() throws IOException {
        var token = lexer.tokenize("var x=.1");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals(".1", token.get(3).getValue());
    }

    @Test
    void parseDoubleDigits() throws IOException {
        var token = lexer.tokenize("var x=.12");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals(".12", token.get(3).getValue());
    }

    @Test
    void parseFloatWithDatatype() throws IOException {
        var floatToken = lexer.tokenize("var x=.1f");
        Assertions.assertSame(TokenType.Decimal, floatToken.get(3).getType());
        Assertions.assertEquals(".1f", floatToken.get(3).getValue());
    }

    @Test
    void parseDoubleWithDatatype() throws IOException {
        var doubleToken = lexer.tokenize("var x=.1d");
        Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
        Assertions.assertEquals(".1d", doubleToken.get(3).getValue());
    }

    @Test
    void parseLongFloatWithDatatype() throws IOException {
        var doubleToken = lexer.tokenize("var x=.11f");
        Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
        Assertions.assertEquals(".11f", doubleToken.get(3).getValue());
    }

    @Test
    void parseLongFloatWithDatatypeRepeat() throws IOException {
        Assertions.assertThrows(LexerTokenException.class, () -> {
            var doubleToken = lexer.tokenize("var x=.11ff");
            Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
            Assertions.assertEquals(".11f", doubleToken.get(3).getValue());
        });
    }

    @Test
    void parseLongDoubleWithDatatype() throws IOException {
        var doubleToken = lexer.tokenize("var x=.11d");
        Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
        Assertions.assertEquals(".11d", doubleToken.get(3).getValue());
    }
    @Test
    void parseLongWith() throws IOException {
        var doubleToken = lexer.tokenize("var x=11.11");
        Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
        Assertions.assertEquals("11.11", doubleToken.get(3).getValue());
    }

    @Test
    void parseLongDoubleWithDatatypeRepeat() throws IOException {
        Assertions.assertThrows(LexerTokenException.class, () -> {
            var doubleToken = lexer.tokenize("var x=.11dd");
            Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
            Assertions.assertEquals(".11d", doubleToken.get(3).getValue());
        });
    }

    @Test
    void parseLongBinaryOp() throws IOException {
        Assertions.assertThrows(LexerTokenException.class, () -> {
            var doubleToken = lexer.tokenize("var x=.11dd");
            Assertions.assertSame(TokenType.Decimal, doubleToken.get(3).getType());
            Assertions.assertEquals(".11d", doubleToken.get(3).getValue());
        });
    }

}
