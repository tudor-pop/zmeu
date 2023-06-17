package dev.fangscl.Runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Parser.Parser;
import dev.fangscl.Runtime.Environment.Environment;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected Interpreter interpreter;
    protected Parser parser;
    protected Tokenizer tokenizer;
    protected Environment global;
    protected ObjectMapper gson = new ObjectMapper();

    @BeforeEach
    void reset() {
        this.global = new Environment();
        this.interpreter = new Interpreter(global);
        this.parser = new Parser();
        this.tokenizer = new Tokenizer();
    }

    protected String toJson(Object o) {
        try {
            return gson.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void testList() {
//        var evalRes = interpreter.eval(Arrays.asList("+", 1, 2, Arrays.asList("*", 2, 3)), new Environment());
//        Assertions.assertEquals(3, ((IntegerValue) evalRes).getValue());
//    }
}