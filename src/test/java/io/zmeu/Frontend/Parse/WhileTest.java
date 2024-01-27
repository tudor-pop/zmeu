package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.AssignmentExpression;
import io.zmeu.Frontend.Parser.Expressions.BinaryExpression;
import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Program;
import io.zmeu.Frontend.Parser.Statements.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class WhileTest extends BaseTest {

    @Test
    void test() {
        var res = parse("""
                while (x>10) { 
                    x+=1
                }
                """);
        var expected = Program.of(
                WhileStatement.builder()
                        .test(BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(10), ">"))
                        .body(ExpressionStatement.of(BlockExpression
                                .of(ExpressionStatement
                                        .of(AssignmentExpression
                                                .of("+=", Identifier.of("x"), NumericLiteral.of(1))
                                        )
                                )
                        )).build()

        );
        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testFor() {
        var res = parse("""
                for (var i=0; i<10; i+=1) { 
                    x+=1
                }
                """);
        var expected = Program.of(
                ForStatement.builder()
                        .init(VariableStatement.builder()
                                .declarations(List.of(VariableDeclaration.of(Identifier.of("i"), NumericLiteral.of(0))))
                                .build())
                        .test(BinaryExpression.of(Identifier.of("i"), NumericLiteral.of(10), "<"))
                        .update(AssignmentExpression.of("+=", Identifier.of("i"), NumericLiteral.of(1)))
                        .body(ExpressionStatement.of(BlockExpression.of(
                                ExpressionStatement.of(
                                        AssignmentExpression.of("+=", Identifier.of("x"), NumericLiteral.of(1))
                                )
                        )))
                        .build()


        );

        log.info(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testForInfinity() {
        var res = parse("""
                for (; ; ) { 
                    x+=1
                }
                """);
        var expected = Program.of(ForStatement.builder()
                .init(null)
                .update(null)
                .body(ExpressionStatement.of(BlockExpression.of(
                        ExpressionStatement.of(
                                AssignmentExpression.of("+=", Identifier.of("x"), NumericLiteral.of(1))
                        )
                )))
                .build()
        );

        log.info(toJson(res));
        assertEquals(expected, res);
    }


}
