package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parser.Literals.NullLiteral;
import io.zmeu.types.Types;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Literals.BooleanLiteral.bool;

@Log4j2
class BooleanTest extends BaseChecker {

    @Test
    void testTrue() {
        var t1 = checker.eval(true);
        Assertions.assertEquals(t1, Types.Boolean);
    }

    @Test
    void testFalse() {
        var t1 = checker.eval(false);
        Assertions.assertEquals(t1, Types.Boolean);
    }

    @Test
    void testTrueLiteral() {
        var t1 = checker.eval(bool(true));
        Assertions.assertEquals(t1, Types.Boolean);
    }

    @Test
    void testFalseLiteral() {
        var t1 = checker.eval(bool(false));
        Assertions.assertEquals(t1, Types.Boolean);
    }

    @Test
    void testNull() {
        var t1 = checker.eval(NullLiteral.of());
        Assertions.assertEquals(t1, Types.Null);
    }

}