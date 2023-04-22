package dev.fangscl.Runtime;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;

class BaseTest {
    protected Interpreter interpreter;
    protected Gson gson = new Gson();

    @BeforeEach
    void reset() {
        this.interpreter = new Interpreter();
    }



//    @Test
//    void testList() {
//        var evalRes = interpreter.eval(Arrays.asList("+", 1, 2, Arrays.asList("*", 2, 3)), new Environment());
//        Assertions.assertEquals(3, ((IntegerValue) evalRes).getValue());
//    }
}