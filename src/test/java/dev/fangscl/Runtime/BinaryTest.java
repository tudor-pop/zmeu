package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.BinaryExpression;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Runtime.Values.IntegerValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BinaryTest extends BaseTest{
    private Interpreter interpreter;
    private Environment environment;

    @BeforeEach
    void init() {
        environment = new Environment();
        interpreter = new Interpreter();
    }

    /**
     * (+ 1 1)
     */
    @Test
    void sum() {
        var res = interpreter.eval(Program.of(ExpressionStatement.of(BinaryExpression.of("+",1,1))));
        var expected = IntegerValue.of(2);
        Assertions.assertEquals(expected, res);
    }

}
