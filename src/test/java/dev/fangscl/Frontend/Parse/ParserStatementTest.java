package dev.fangscl.Frontend.Parse;

import com.google.gson.Gson;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Parser.Parser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class ParserStatementTest {
    protected Parser parser;
    protected Tokenizer tokenizer;
    protected Gson gson = new Gson();


    @BeforeAll
    static void setLog4j() {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
    }

    @BeforeEach
    void init() {
        tokenizer = new Tokenizer();
        parser = new Parser();
    }


}
