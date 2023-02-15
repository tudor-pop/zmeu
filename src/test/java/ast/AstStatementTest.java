package ast;

import com.google.gson.Gson;
import dev.fangscl.Parsing.Parser;
import dev.fangscl.Parsing.Lexer.Lexer;
import org.junit.jupiter.api.BeforeEach;

class AstStatementTest {
    protected Parser parser;
    protected Gson gson = new Gson();


    @BeforeEach
    void init() {
        parser = new Parser(new Lexer());
    }


}
