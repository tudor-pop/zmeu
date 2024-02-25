package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Statements.VariableStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.program;
import static io.zmeu.Frontend.Parser.Factory.type;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class TypeTest extends BaseTest {

    @Test
    void testInteger() {
        var res = parse("var x:String\n");
        var expected = program(VariableStatement.of(VariableDeclaration.of(Identifier.of("x"),type("String"), null)));
        assertEquals(expected, res);
        log.info(toJson(res));
    }



}
