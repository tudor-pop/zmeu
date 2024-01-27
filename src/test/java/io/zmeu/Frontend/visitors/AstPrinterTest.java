package io.zmeu.Frontend.visitors;

import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Expressions.GroupExpression;
import io.zmeu.Frontend.Parser.Expressions.LogicalExpression;
import io.zmeu.Frontend.Parser.Expressions.UnaryExpression;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Log4j2
class AstPrinterTest {
    private final AstPrinter printer = new AstPrinter();

    @Test
    void numeric() {
        var res = printer.print(NumericLiteral.of("2"));
        log.warn(res);
        Assertions.assertEquals("2", res);
    }

    @Test
    void logical() {
        var res = printer.print(LogicalExpression.of(">", NumericLiteral.of("2"), NumericLiteral.of("3")));
        log.warn(res);
        Assertions.assertEquals("(> 2 3)", res);
    }

    @Test
    void group() {
        var res = printer.print(new GroupExpression(NumericLiteral.of(2)));
        log.warn(res);
        Assertions.assertEquals("(group 2)", res);
    }

    @Test
    void groupComplex() {
        var expr = BinaryExpression.of("*",
                UnaryExpression.of("-", NumericLiteral.of(2)),
                new GroupExpression(NumericLiteral.of(3)));
        var res = printer.print(expr);
        log.warn(res);
        Assertions.assertEquals("(* (- 2) (group 3))", res);
    }

}