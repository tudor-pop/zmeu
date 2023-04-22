package dev.fangscl.Runtime;

import com.google.gson.Gson;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Parser.Parser;
import org.junit.jupiter.api.BeforeEach;

class BaseTest {
    protected Interpreter interpreter;
    protected Parser parser;
    protected Tokenizer tokenizer;
    protected Environment environment;
    protected Gson gson = new Gson();

    @BeforeEach
    void reset() {
        this.environment = new Environment();
        this.interpreter = new Interpreter(environment);
        this.parser = new Parser();
        this.tokenizer = new Tokenizer();
    }



//    @Test
//    void testList() {
//        var evalRes = interpreter.eval(Arrays.asList("+", 1, 2, Arrays.asList("*", 2, 3)), new Environment());
//        Assertions.assertEquals(3, ((IntegerValue) evalRes).getValue());
//    }
}