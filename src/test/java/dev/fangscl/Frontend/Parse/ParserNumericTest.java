package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ParserNumericTest extends ParserStatementTest {

    @Test
    void testInteger() {
        var res = parser.produceAST("1");
        Statement expression = res.first();
        assertEquals(NodeType.IntegerLiteral, expression.getKind());
        assertEquals(1, ((NumericLiteral) expression).getValue());
    }

    @Test
    void testDecimal() {
        var res = parser.produceAST("1.11");
        Statement expression = res.first();
        assertEquals(NodeType.DecimalLiteral, expression.getKind());
        assertEquals(1.11, ((NumericLiteral) expression).getValue());
    }

    @Test
    void testAddition() {
        var res = parser.produceAST("1 + 1");
        String expression = gson.toJson(res);
        assertEquals("""
                        {"body":[{"left":{"value":1,"kind":"IntegerLiteral"},"right":{"value":1,"kind":"IntegerLiteral"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
        Statement actualValue = res.getBody().get(0);
        assertInstanceOf(BinaryExpression.class, actualValue);
    }

    @Test
    void testAddition3() {
        var res = parser.produceAST("1 + 1+1");
        String expression = gson.toJson(res);
        assertEquals("""
                        {"body":[{"left":{"left":{"value":1,"kind":"IntegerLiteral"},"right":{"value":1,"kind":"IntegerLiteral"},"operator":"+","kind":"BinaryExpression"},"right":{"value":1,"kind":"IntegerLiteral"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }

    @Test
    void testAdditionSubstraction3() {
        var res = parser.produceAST("1 + 1-11");
        String expression = gson.toJson(res);
        assertEquals("""
                        {"body":[{"left":{"left":{"value":1,"kind":"IntegerLiteral"},"right":{"value":1,"kind":"IntegerLiteral"},"operator":"+","kind":"BinaryExpression"},"right":{"value":11,"kind":"IntegerLiteral"},"operator":"-","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }

    @Test
    void testAdditionMultiplication() {
        var res = parser.produceAST("1 + 2*3");
        String expression = gson.toJson(res);
        assertEquals("""
                        {"body":[{"left":{"value":1,"kind":"IntegerLiteral"},"right":{"left":{"value":2,"kind":"IntegerLiteral"},"right":{"value":3,"kind":"IntegerLiteral"},"operator":"*","kind":"BinaryExpression"},"operator":"+","kind":"BinaryExpression"}],"kind":"Program"}"""
                , expression);
    }

    @Test
    void testAdditionParanthesis() {
        var res = parser.produceAST("1 + 2 - (3*4)");
        String expression = gson.toJson(res);
        assertEquals("""
                        {"body":[{"left":{"left":{"value":1,"kind":"IntegerLiteral"},"right":{"value":2,"kind":"IntegerLiteral"},"operator":"+","kind":"BinaryExpression"},"right":{"left":{"value":3,"kind":"IntegerLiteral"},"right":{"value":4,"kind":"IntegerLiteral"},"operator":"*","kind":"BinaryExpression"},"operator":"-","kind":"BinaryExpression"}],"kind":"Program"}"""
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
