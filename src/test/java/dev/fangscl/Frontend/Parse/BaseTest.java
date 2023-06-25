package dev.fangscl.Frontend.Parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Parser.Parser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


class BaseTest {
    protected Parser parser;
    protected Tokenizer tokenizer;
    protected ObjectMapper gson = new ObjectMapper();


    @BeforeAll
    static void setLog4j() {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
    }

    @BeforeEach
    void init() {
        tokenizer = new Tokenizer();
        parser = new Parser();
    }

    protected Object parse(String source) {
        return parser.produceAST(tokenizer.tokenize(source));
    }

    protected String toJson(Object o) {
        try {
            return gson.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
