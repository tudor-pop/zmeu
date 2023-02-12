package ast;

import com.google.gson.Gson;
import dev.fangscl.ast.NodeType;
import dev.fangscl.ast.statements.expressions.IdentifierExpression;
import dev.fangscl.ast.statements.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class AstIdentityTest extends AstStatementTest {
    private Gson gson = new Gson();

    @Test
    void testCreate() {
        var res = parser.produceAST("var x = 1");
        Statement expression = res.getBody().get(1);
        Assertions.assertEquals(NodeType.Identifier, expression.getKind());
        Assertions.assertEquals("x", ((IdentifierExpression) expression).getSymbol());
    }

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
