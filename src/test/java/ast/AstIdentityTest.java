package ast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class AstIdentityTest extends AstStatementTest {

//    @Test
//    void testCreate() {
//        var res = parser.produceAST("var x = 1");
//        Statement expression = res.getBody().get(1);
//        Assertions.assertEquals(NodeType.Identifier, expression.getKind());
//        Assertions.assertEquals("x", ((IdentifierExpression) expression).getSymbol());
//    }

    @Test
    void testAddition() {
        var res = parser.produceAST("1 + 1");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"left":{"value":1,"kind":"NumericLiteral"},"right":{"value":1,"kind":"NumericLiteral"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }

    @Test
    void testAddition3() {
        var res = parser.produceAST("1 + 1+1");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"left":{"left":{"value":1,"kind":"NumericLiteral"},"right":{"value":1,"kind":"NumericLiteral"},"operator":"+","kind":"BinaryExpression"},"right":{"value":1,"kind":"NumericLiteral"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }

    @Test
    void testAdditionSubstraction3() {
        var res = parser.produceAST("1 + 1-11");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"left":{"left":{"value":1,"kind":"NumericLiteral"},"right":{"value":1,"kind":"NumericLiteral"},"operator":"+","kind":"BinaryExpression"},"right":{"value":11,"kind":"NumericLiteral"},"operator":"-","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }
    @Test
    void testAdditionMultiplication() {
        var res = parser.produceAST("1 + 2*3");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"left":{"value":1,"kind":"NumericLiteral"},"right":{"left":{"value":2,"kind":"NumericLiteral"},"right":{"value":3,"kind":"NumericLiteral"},"operator":"*","kind":"BinaryExpression"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }
    @Test
    void testAdditionParanthesis() {
        var res = parser.produceAST("1 + 2 - (3*4)");
        String expression = gson.toJson(res);
        Assertions.assertEquals("""
                        {"body":[{"left":{"left":{"value":1,"kind":"NumericLiteral"},"right":{"value":2,"kind":"NumericLiteral"},"operator":"+","kind":"BinaryExpression"},"right":{"left":{"value":3,"kind":"NumericLiteral"},"right":{"value":4,"kind":"NumericLiteral"},"operator":"*","kind":"BinaryExpression"},"operator":"-","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }


    @Test
    void testList() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        var i = list.iterator();
        System.out.println(i.next());
        var j = list.iterator();
        System.out.println(j.next());
    }

}
