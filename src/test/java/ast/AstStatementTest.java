package ast;

import dev.fangscl.ast.Parser;
import dev.fangscl.lexer.Lexer;
import org.junit.jupiter.api.BeforeEach;

class AstStatementTest {
    protected Parser parser;

    @BeforeEach
    void init() {
        parser = new Parser(new Lexer());
    }


}
