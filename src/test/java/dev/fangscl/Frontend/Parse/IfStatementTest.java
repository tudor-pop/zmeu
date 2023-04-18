package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Runtime.TypeSystem.Expressions.AssignmentExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.BlockStatement;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import dev.fangscl.Runtime.TypeSystem.Statements.IfStatement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class IfStatementTest extends StatementTest {

    @Test
    void test() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) { 
                    x=1
                }
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        BlockStatement.of(
                                ExpressionStatement.of(
                                        AssignmentExpression.of(Identifier.of("x"), Literal.of(1), "=")
                                )
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testXNoCurly() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) x=1
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(1), "=")
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testNoCurly() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) 
                    x=1
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(1), "="))));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testIfElseStatementBlocks() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) { 
                    1
                } else { 
                    2
                }
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        BlockStatement.of(ExpressionStatement.of(Literal.of(1))),
                        BlockStatement.of(ExpressionStatement.of(Literal.of(2)))));
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testXBlock() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) {
                    if(y) x=1
                }
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        BlockStatement.of(
                                IfStatement.of(
                                        Identifier.of("y"),
                                        ExpressionStatement.of(
                                                AssignmentExpression.of(Identifier.of("x"), Literal.of(1), "="))
                                )
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testNoBlockY() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) 
                    if(y) x=1
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        IfStatement.of(
                                Identifier.of("y"),
                                ExpressionStatement.of(
                                        AssignmentExpression.of(Identifier.of("x"), Literal.of(1), "="))
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testXCurlyY() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) 
                    if(y){ x=1 }
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        IfStatement.of(
                                Identifier.of("y"),
                                BlockStatement.of(
                                        ExpressionStatement.of(
                                                AssignmentExpression.of(Identifier.of("x"), Literal.of(1), "=")
                                        )
                                )
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testNestedElse() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) 
                    if(y) {} else { }
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        IfStatement.of(
                                Identifier.of("y"),
                                BlockStatement.of(),
                                BlockStatement.of()
                        )
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testNestedElseElse() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) 
                    if(y) {} else { } else {}
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        IfStatement.of(
                                Identifier.of("y"),
                                BlockStatement.of(),
                                BlockStatement.of()
                        ),
                        BlockStatement.of()
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testNestedElseElseInline() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) if(y) {} else { } else {}
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        IfStatement.of(
                                Identifier.of("y"),
                                BlockStatement.of(),
                                BlockStatement.of()
                        ),
                        BlockStatement.of()
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testNestedElseElseAssignInline() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x) if(y) {} else { } else { x=2}
                """));
        var expected = Program.of(
                IfStatement.of(Identifier.of("x"),
                        IfStatement.of(
                                Identifier.of("y"),
                                BlockStatement.of(),
                                BlockStatement.of()
                        ),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "=")
                        ))
                )
        );
        assertEquals(expected, res);
        log.info(gson.toJson(res));
    }

    @Test
    void testRelationalGt() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x > 1) {
                    x = 2;
                } else {
                    x += 2
                }
                """));
        var expected = Program.of(
                IfStatement.of(
                        BinaryExpression.of("x", 1, ">"),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "="))
                        ),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "+="))
                        )));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testRelationalGtEq() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x >= 1) {
                    x = 2;
                } else {
                    x += 2
                }
                """));
        var expected = Program.of(
                IfStatement.of(
                        BinaryExpression.of(Identifier.of("x"), Literal.of(1), ">="),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "="))
                        ),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "+="))
                        )));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testRelationalLt() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x < 1) {
                    x = 2;
                } else {
                    x += 2
                }
                """));
        var expected = Program.of(
                IfStatement.of(
                        BinaryExpression.of(Identifier.of("x"), Literal.of(1), "<"),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "="))
                        ),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "+="))
                        )));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void testRelationalLtEq() {
        var res = parser.produceAST(tokenizer.tokenize("""
                if (x <= 1) {
                    x = 2;
                } else {
                    x += 2
                }
                """));
        var expected = Program.of(
                IfStatement.of(
                        BinaryExpression.of(Identifier.of("x"), Literal.of(1), "<="),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "="))
                        ),
                        BlockStatement.of(ExpressionStatement.of(
                                AssignmentExpression.of(Identifier.of("x"), Literal.of(2), "+="))
                        )));
        log.info(gson.toJson(res));
        assertEquals(expected, res);
    }


}
