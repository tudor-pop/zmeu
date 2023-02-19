package dev.fangscl.Frontend.Parse;

import com.google.gson.Gson;
import dev.fangscl.Frontend.Lexer.Lexer;
import dev.fangscl.Frontend.Parser.Parser;
import org.junit.jupiter.api.BeforeEach;

class ParserStatementTest {
    protected Parser parser;
    protected Gson gson = new Gson();


    @BeforeEach
    void init() {
        parser = new Parser(new Lexer());
    }


}
