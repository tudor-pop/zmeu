package ast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NullLiteralTest extends AstLiteralTest{
    @Test
    void testAdditionParanthesis() {
        var res = parser.produceAST("null");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"value":"null","kind":"NumericLiteral"}],"kind":"Program"}"""
                , expression);
    }
    @Test
    void testAdditionNull() {
        var res = parser.produceAST("10+null");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"left":{"value":10,"kind":"NumericLiteral"},"right":{"value":"null","kind":"NumericLiteral"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }

}
