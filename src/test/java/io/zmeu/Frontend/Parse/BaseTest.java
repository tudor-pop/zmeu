package io.zmeu.Frontend.Parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.Lexer.Tokenizer;
import io.zmeu.Frontend.Parser.Parser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


public class BaseTest {
    protected Parser parser;
    protected Tokenizer tokenizer;
    protected ObjectMapper mapper = new ObjectMapper();


    @BeforeAll
    static void setLog4j() {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
    }

    @BeforeEach
    void init() {
        tokenizer = new Tokenizer();
        parser = new Parser();
        mapper =  JsonMapper.builder() // or different mapper for other format
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
        ErrorSystem.clear();
    }

    protected Object parse(String source) {
        return parser.produceAST(tokenizer.tokenize(source));
    }

    protected String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
