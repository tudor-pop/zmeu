package dev.fangscl.Runtime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Lexical.Resolver;
import dev.fangscl.Frontend.Parser.Parser;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Runtime.Environment.Environment;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected Interpreter interpreter;
    protected Parser parser;
    protected Tokenizer tokenizer;
    protected Environment global;
    protected ObjectMapper gson = new ObjectMapper();
    protected Resolver resolver;

    @BeforeEach
    void reset() {
        this.global = new Environment();
        this.interpreter = new Interpreter(global);
        this.parser = new Parser();
        this.tokenizer = new Tokenizer();
        this.resolver = new Resolver(interpreter);
        gson.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        gson.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        gson.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        gson.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    protected String toJson(Object o) {
        try {
            return gson.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object eval(String source) {
        Program program = parser.produceAST(tokenizer.tokenize(source));
        resolver.resolve(program);
        return interpreter.eval(program);
    }

    protected Object interpret(String source) {
        return interpreter.eval(parser.produceAST(tokenizer.tokenize(source)));
    }

    protected Object resolve(String source) {
         resolver.resolve(parser.produceAST(tokenizer.tokenize(source)));
         return null;
    }

//    @Test
//    void testList() {
//        var evalRes = interpreter.eval(Arrays.asList("+", 1, 2, Arrays.asList("*", 2, 3)), new Environment());
//        Assertions.assertEquals(3, ((IntegerValue) evalRes).getValue());
//    }
}