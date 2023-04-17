package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.TypeSystem.Expressions.AssignmentExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.VariableDeclaration;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LoopStatementTest extends StatementTest {

    @Test
    void test() {
        var res = parser.produceAST(tokenizer.tokenize("""
                while (x>10) { 
                    x+=1
                }
                """));
        var expected = Program.of(
                WhileStatement.builder()
                        .test(BinaryExpression.of(Identifier.of("x"), NumericLiteral.of(10), ">"))
                        .body(BlockStatement
                                .of(ExpressionStatement
                                        .of(AssignmentExpression
                                                .of("+=", Identifier.of("x"), Literal.of(1))
                                        )
                                )
                        ).build()

        );
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testFor() {
        var res = parser.produceAST(tokenizer.tokenize("""
                for (var i=0; i<10; i+=1) { 
                    x+=1
                }
                """));
        var expected = Program.of(
                ForStatement.builder()
                        .init(VariableStatement.builder()
                                .declarations(List.of(VariableDeclaration.of(Identifier.of("i"), NumericLiteral.of(0))))
                                .build())
                        .test(BinaryExpression.of(Identifier.of("i"), NumericLiteral.of(10), "<"))
                        .update(AssignmentExpression.of("+=", Identifier.of("i"), NumericLiteral.of(1)))
                        .body(BlockStatement.of(
                                ExpressionStatement.of(
                                        AssignmentExpression.of("+=", Identifier.of("x"), Literal.of(1))
                                )
                        ))
                        .build()


        );

        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testForInfinity() {
        var res = parser.produceAST(tokenizer.tokenize("""
                for (; ; ) { 
                    x+=1
                }
                """));
        var expected = Program.of(ForStatement.builder()
                .init(null)
                .update(null)
                .body(BlockStatement.of(
                        ExpressionStatement.of(
                                AssignmentExpression.of("+=", Identifier.of("x"), Literal.of(1))
                        )
                ))
                .build()
        );

        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }


}
