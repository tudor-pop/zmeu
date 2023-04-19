package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.TypeSystem.Expressions.CallExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.MemberExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class CallExpressionTest extends BaseTest {

    @Test
    void testFunctionCall() {
        var res = parser.produceAST(tokenizer.tokenize("foo(x)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of("foo", "x")));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testCallReturnedFunction() {
        var res = parser.produceAST(tokenizer.tokenize("foo(x)()"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of(CallExpression.of("foo", "x"), Collections.emptyList())
        ));

        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }
    @Test
    void testMemberAssignment() {
        var res = parser.produceAST(tokenizer.tokenize("console.log(x,y)"));
        var expected = Program.of(ExpressionStatement.of(
                CallExpression.of(
                        MemberExpression.of(false,"console", "log"),
                        Identifier.of("x", "y"))
        ));

        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }


}
