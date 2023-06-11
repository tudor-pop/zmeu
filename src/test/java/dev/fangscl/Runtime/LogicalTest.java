package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.LogicalExpression;
import dev.fangscl.Frontend.Parser.Literals.BooleanLiteral;
import dev.fangscl.Frontend.Parser.Literals.NullLiteral;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Runtime.Values.IntegerValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogicalTest extends BaseTest {

    @Test
    void trueOrTrue() {
        // true || true -> true
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("||",
                BooleanLiteral.of(true), BooleanLiteral.of(true)))));
        var expected = true;
        Assertions.assertEquals(expected, res);
    }

    @Test
    void trueOrFalse() {
        // true || false -> true
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("||",
                BooleanLiteral.of(true), BooleanLiteral.of(false)))));
        var expected = true;
        Assertions.assertEquals(expected, res);
    }

    @Test
    void falseOrTrue() {
        // false || true -> true
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("||",
                BooleanLiteral.of(false), BooleanLiteral.of(true)))));
        var expected = true;
        Assertions.assertEquals(expected, res);
    }

    @Test
    void falseOrFalse() {
        // false || true -> true
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("||",
                BooleanLiteral.of(false), BooleanLiteral.of(false)))));
        var expected = false;
        Assertions.assertEquals(expected, res);
    }

    @Test
    void falseAndTrue() {
        // null && 2 -> null
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("&&",
                NullLiteral.of(), NumericLiteral.of(2)))));
        Assertions.assertEquals(null, res);
    }

    @Test
    void trueAndFalse() {
        // 2 && null -> null
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("&&",
                NumericLiteral.of(2), NullLiteral.of()))));
        Assertions.assertEquals(null, res);
    }

    @Test
    void trueAndTrue() {
        // 1 && 2 -> 2
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("&&",
                NumericLiteral.of(1), NumericLiteral.of(2)))));
        var expected = IntegerValue.of(2);
        Assertions.assertEquals(expected, res);
    }

    @Test
    void falseAndFalse() {
        // false && false -> false
        var res = interpreter.eval(Program.of(ExpressionStatement.of(LogicalExpression.of("&&",
                BooleanLiteral.of(false), BooleanLiteral.of(false)))));
        var expected = false;
        Assertions.assertEquals(expected, res);
    }

}
