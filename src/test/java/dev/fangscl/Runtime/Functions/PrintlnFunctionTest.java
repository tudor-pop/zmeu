package dev.fangscl.Runtime.Functions;

import dev.fangscl.Runtime.BaseTest;
import org.junit.jupiter.api.Test;

class PrintlnFunctionTest extends BaseTest {

    private final PrintlnFunction function = new PrintlnFunction();

    @Test
    void printlnNumbers() {
        function.call(1, 2, 3);
    }

    @Test
    void printlnStringNums() {
        function.call("1", "2", "3");
    }

    @Test
    void printlnMix() {
        function.call("1", "2", 3);
    }

    @Test
    void printlnMixStrings() {
        function.call("hi", "!", "I'm", "Bob", 3);
    }

    @Test
    void printlnStrings() {
        interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                println(1,2,3)
                println("hi!","my","name","is","Bob")
                println("hi! my name is Bob")
                """)));
    }
}