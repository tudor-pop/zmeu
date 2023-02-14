package ast;

import dev.fangscl.Parsing.Parser;
import dev.fangscl.Parsing.Lexer.Lexer;
import org.junit.jupiter.api.BeforeEach;

class AstStatementTest {
    protected Parser parser;

    @BeforeEach
    void init() {
        parser = new Parser(new Lexer());
    }


}
