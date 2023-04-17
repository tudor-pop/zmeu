package dev.fangscl.Frontend.Token;

import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
public class TokenizerTest {
    private Tokenizer tokenizer;

    @BeforeEach
    void beforeEach() {
        tokenizer = new Tokenizer();
    }

    @Test
    void testOneDigit() {
        var result = tokenizer.tokenizeLiteral("1");
        Assertions.assertEquals(TokenType.Number, result.getType());
        Assertions.assertEquals(1, result.getValue());
        log.info(result);
    }

    @Test
    void testMultipleDigits() {
        var result = tokenizer.tokenizeLiteral("422");
        Assertions.assertEquals(TokenType.Number, result.getType());
        Assertions.assertEquals(422, result.getValue());
        log.info(result);
    }

    @Test
    void testDecimal() {
        var result = tokenizer.tokenizeLiteral("1.2");
        Assertions.assertEquals(TokenType.Number, result.getType());
        Assertions.assertEquals(1.2f, result.getValue());
        log.info(result);
    }


    @Test
    void testSpace() {
        var result = tokenizer.tokenizeLiteral("  ");
        Assertions.assertEquals(TokenType.EOF, result.getType());
        log.info(result);
    }

    @Test
    void testLiteralStringNumber() {
        var result = tokenizer.tokenizeLiteral("""
                "422"
                """);
        Assertions.assertEquals(TokenType.String, result.getType());
        Assertions.assertEquals("\"422\"", result.getValue());
        log.info(result);
    }

    @Test
    void testLiteralString() {
        var result = tokenizer.tokenizeLiteral("\"hello\"");
        Assertions.assertEquals(TokenType.String, result.getType());
        Assertions.assertEquals("\"hello\"", result.getValue());
        log.info(result);
    }

    @Test
    void testLineTerminator() {
        var result = tokenizer.tokenizeLiteral("\n");
        Assertions.assertEquals(TokenType.NewLine, result.getType());
        Assertions.assertEquals("\n", result.getValue());
        log.info(result);
    }

    @Test
    void testPlus() {
        var result = tokenizer.tokenizeLiteral("+");
        Assertions.assertEquals(TokenType.Plus, result.getType());
        Assertions.assertEquals("+", result.getValue());
        log.info(result);

    }
    @Test
    void testMinus() {
        var result = tokenizer.tokenizeLiteral("-");
        Assertions.assertEquals(TokenType.Minus, result.getType());
        Assertions.assertEquals("-", result.getValue());
        log.info(result);
    }
    @Test
    void testMultiplication() {
        var result = tokenizer.tokenizeLiteral("*");
        Assertions.assertEquals(TokenType.Multiply, result.getType());
        Assertions.assertEquals("*", result.getValue());
        log.info(result);
    }

    @Test
    void testLineTerminatorComplex() {
        var result = tokenizer.tokenize("1+1\n");
        Assertions.assertEquals(TokenType.NewLine, result.get(3).getType());
        Assertions.assertEquals("\n", result.get(3).getValue());
        log.info(result);
    }

    @Test
    void testMultilineComment() {
        var result = tokenizer.tokenizeLiteral("""
                 /* 
                  * "hello" 
                  */
                  "Str"
                """);
        Assertions.assertEquals(TokenType.String, result.getType());
        Assertions.assertEquals("\"Str\"", result.getValue());
        log.info(result);
    }

    @Test
    void testLiteralSingleQuoteString() {
        var result = tokenizer.tokenizeLiteral("'hello'");
        Assertions.assertEquals(TokenType.String, result.getType());
        Assertions.assertEquals("\'hello\'", result.getValue());
        log.info(result);
    }

    @Test
    void testLiteralWhitespaceString() {
        var result = tokenizer.tokenizeLiteral("   42    ");
        Assertions.assertEquals(TokenType.Number, result.getType());
        Assertions.assertEquals(42, result.getValue());
        log.info(result);
    }

    @Test
    void testLiteralWhitespaceStringInside() {
        var result = tokenizer.tokenizeLiteral("   \"  42  \"    ");
        Assertions.assertEquals(TokenType.String, result.getType());
        Assertions.assertEquals("\"  42  \"", result.getValue());
        log.info(result);
    }

    @Test
    void testOpenParanthesis() {
        var result = tokenizer.tokenizeLiteral("(");
        Assertions.assertEquals(TokenType.OpenParenthesis, result.getType());
        Assertions.assertEquals("(", result.getValue());
        log.info(result);
    }

    @Test
    void testCloseParanthesis() {
        var result = tokenizer.tokenizeLiteral(")");
        Assertions.assertEquals(TokenType.CloseParenthesis, result.getType());
        Assertions.assertEquals(")", result.getValue());
        log.info(result);
    }

    @Test
    void testOpenBraces() {
        var result = tokenizer.tokenizeLiteral("{");
        Assertions.assertEquals(TokenType.OpenBraces, result.getType());
        Assertions.assertEquals("{", result.getValue());
        log.info(result);
    }

    @Test
    void testCloseBraces() {
        var result = tokenizer.tokenizeLiteral("}");
        Assertions.assertEquals(TokenType.CloseBraces, result.getType());
        Assertions.assertEquals("}", result.getValue());
        log.info(result);
    }
    @Test
    void testOpenBrackets() {
        var result = tokenizer.tokenizeLiteral("[");
        Assertions.assertEquals(TokenType.OpenBrackets, result.getType());
        Assertions.assertEquals("[", result.getValue());
        log.info(result);
    }

    @Test
    void testCloseBrackets() {
        var result = tokenizer.tokenizeLiteral("]");
        Assertions.assertEquals(TokenType.CloseBrackets, result.getType());
        Assertions.assertEquals("]", result.getValue());
        log.info(result);
    }

    @Test
    void testNotEquals() {
        var result = tokenizer.tokenizeLiteral("!=");
        Assertions.assertEquals(TokenType.Equality_Operator, result.getType());
        Assertions.assertEquals("!=", result.getValue());
        log.info(result);
    }

    @Test
    void testEqualsEquals() {
        var result = tokenizer.tokenizeLiteral("==");
        Assertions.assertEquals(TokenType.Equality_Operator, result.getType());
        Assertions.assertEquals("==", result.getValue());
        log.info(result);
    }

    @Test
    void testLessEquals() {
        var result = tokenizer.tokenizeLiteral("<=");
        Assertions.assertEquals(TokenType.RelationalOperator, result.getType());
        Assertions.assertEquals("<=", result.getValue());
        log.info(result);
    }

    @Test
    void testLess() {
        var result = tokenizer.tokenizeLiteral("<");
        Assertions.assertEquals(TokenType.RelationalOperator, result.getType());
        Assertions.assertEquals("<", result.getValue());
        log.info(result);
    }

    @Test
    void testGreater() {
        var result = tokenizer.tokenizeLiteral(">");
        Assertions.assertEquals(TokenType.RelationalOperator, result.getType());
        Assertions.assertEquals(">", result.getValue());
        log.info(result);
    }

    @Test
    void testGreaterEquals() {
        var result = tokenizer.tokenizeLiteral(">=");
        Assertions.assertEquals(TokenType.RelationalOperator, result.getType());
        Assertions.assertEquals(">=", result.getValue());
        log.info(result);
    }

    @Test
    void testDivision() {
        var result = tokenizer.tokenizeLiteral("/");
        Assertions.assertEquals(TokenType.Division, result.getType());
        Assertions.assertEquals("/", result.getValue());
        log.info(result);
    }
// Complex strings
    @Test
    void testOpenBracesWithText() {
        var result = tokenizer.tokenizeLiteral("{ \"hey\" }");
        Assertions.assertEquals(TokenType.OpenBraces, result.getType());
        Assertions.assertEquals("{", result.getValue());
        log.info(result);
    }
    @Test
    void testOpenNested() {
        var result = tokenizer.tokenize("{ { \"hey\" ");
        Assertions.assertEquals(TokenType.OpenBraces, result.get(0).getType());
        Assertions.assertEquals("{", result.get(0).getValue());
        Assertions.assertEquals(TokenType.OpenBraces, result.get(1).getType());
        Assertions.assertEquals("{", result.get(1).getValue());
        log.info(result);
    }

    ////////// COMMENTS /////////
    @Test
    void testCommentIsIgnored() {
        var result = tokenizer.tokenizeLiteral("// a comment goes until the end of line \n");
        Assertions.assertEquals("EOF", result.getValue());
        log.info(result);
    }

    @Test
    void testNumberOnNextLineAfterComment() {
        var result = tokenizer.tokenizeLiteral("""
                // a comment goes until the end of line 
                10
                """);
        Assertions.assertEquals(TokenType.Number, result.getType());
        Assertions.assertEquals(10, result.getValue());
        log.info(result);
    }

    @Test
    void testCommentIgnoredAfterVar() {
        var result = tokenizer.tokenize("var x=23 // a comment goes until the end of line 10");
        Assertions.assertEquals(TokenType.Var, result.get(0).getType());
        Assertions.assertEquals("var", result.get(0).getValue());
        Assertions.assertEquals(TokenType.Identifier, result.get(1).getType());
        Assertions.assertEquals("x", result.get(1).getValue());
        Assertions.assertEquals(TokenType.Equal, result.get(2).getType());
        Assertions.assertEquals("=", result.get(2).getValue());
        Assertions.assertEquals(TokenType.Number, result.get(3).getType());
        Assertions.assertEquals(23, result.get(3).getValue());
        Assertions.assertEquals(TokenType.EOF, result.get(4).getType());
        Assertions.assertEquals("EOF", result.get(4).getValue());
        log.info(result);
    }

    @Test
    void testUnknownIdentifier() {
        var result = tokenizer.tokenizeLiteral("tudor");
        Assertions.assertEquals(TokenType.Identifier, result.getType());
        Assertions.assertEquals("tudor", result.getValue());
        log.info(result);
    }

    @Test
    void testKeywordVar() {
        var result = tokenizer.tokenizeLiteral("var");
        Assertions.assertEquals(TokenType.Var, result.getType());
        Assertions.assertEquals("var", result.getValue());
        log.info(result);
    }

    @Test
    void testKeywordModule() {
        var result = tokenizer.tokenizeLiteral("module");
        Assertions.assertEquals(TokenType.Module, result.getType());
        Assertions.assertEquals("module", result.getValue());
        log.info(result);
    }

    @Test
    void testKeywordParam() {
        var result = tokenizer.tokenizeLiteral("param");
        Assertions.assertEquals(TokenType.Param, result.getType());
        Assertions.assertEquals("param", result.getValue());
        log.info(result);
    }

    @Test
    void testComplex() {
        var result = tokenizer.tokenize("var x=10");
        Assertions.assertEquals(TokenType.Var, result.get(0).getType());
        Assertions.assertEquals("var", result.get(0).getValue());
        Assertions.assertEquals(TokenType.Identifier, result.get(1).getType());
        Assertions.assertEquals("x", result.get(1).getValue());
        Assertions.assertEquals(TokenType.Equal, result.get(2).getType());
        Assertions.assertEquals("=", result.get(2).getValue());
        Assertions.assertEquals(TokenType.Number, result.get(3).getType());
        Assertions.assertEquals(10, result.get(3).getValue());
        log.info(result);
    }

    @Test
    void testComplexWithSpace() {
        var result = tokenizer.tokenize("var xuru   =    10");
        Assertions.assertEquals(TokenType.Var, result.get(0).getType());
        Assertions.assertEquals("var", result.get(0).getValue());
        Assertions.assertEquals(TokenType.Identifier, result.get(1).getType());
        Assertions.assertEquals("xuru", result.get(1).getValue());
        Assertions.assertEquals(TokenType.Equal, result.get(2).getType());
        Assertions.assertEquals("=", result.get(2).getValue());
        Assertions.assertEquals(TokenType.Number, result.get(3).getType());
        Assertions.assertEquals(10, result.get(3).getValue());
        log.info(result);
    }

    @Test
    void testComplexWithSpaceWithName() {
        var result = tokenizer.tokenize("var variable");
        Assertions.assertEquals(TokenType.Var, result.get(0).getType());
        Assertions.assertEquals("var", result.get(0).getValue());
        Assertions.assertEquals(TokenType.Identifier, result.get(1).getType());
        Assertions.assertEquals("variable", result.get(1).getValue());
        log.info(result);
    }

    @Test
    void testUnexpected() {
        var result = tokenizer.tokenizeLiteral("&");
        log.info(result);
    }

}
