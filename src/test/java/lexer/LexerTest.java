package lexer;

import dev.fangscl.Parsing.Lexer;
import dev.fangscl.Parsing.TokenType;
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
    void parseSingleDecimalNumber() throws IOException {
        var token = lexer.tokenize("var x=.1");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals(".1", token.get(3).getValue());
    }

    @Test
    void parseDoubleDecimalNumbers() throws IOException {
        var token = lexer.tokenize("var x=.12");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals(".12", token.get(3).getValue());
    }

    @Test
    void parseDecimalWithPrefix() throws IOException {
        var token = lexer.tokenize("var x=0.12");
        Assertions.assertSame(TokenType.Decimal, token.get(3).getType());
        Assertions.assertEquals("0.12", token.get(3).getValue());
    }

}
