package dev.fangscl.Runtime;

import com.google.gson.Gson;
import dev.fangscl.Parsing.Lexer.Lexer;
import dev.fangscl.Parsing.Parser;
import dev.fangscl.Runtime.Scope.Scope;
import dev.fangscl.Runtime.Values.IntegerValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterpreterTest {
    protected Parser parser;
    protected Gson gson;
    protected Interpreter interpreter;

    @BeforeEach
    void reset() {
        this.parser = new Parser(new Lexer());
        this.interpreter = new Interpreter();
    }

    @Test
    void testVariableEvaluation() {
        Scope scope = new Scope();
        scope.declareVar("x", new IntegerValue(10));
        var program = parser.produceAST("x");
        var evalRes = interpreter.eval(program, scope);
        Assertions.assertInstanceOf(IntegerValue.class, evalRes);
        Assertions.assertEquals(10, ((IntegerValue) evalRes).getValue());
    }

}