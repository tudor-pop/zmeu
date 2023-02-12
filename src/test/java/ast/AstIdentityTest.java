package ast;

import dev.fangscl.ast.NodeType;
import dev.fangscl.ast.statements.IdentifierExpression;
import dev.fangscl.ast.statements.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AstIdentityTest extends AstStatementTest {

    @Test
    void testCreate() {
        var res = parser.produceAST("var x = 1");
        Statement expression =  res.getBody().get(1);
        Assertions.assertEquals(NodeType.Identifier, expression.getKind());
        Assertions.assertEquals("x", ((IdentifierExpression) expression).getSymbol());
    }

}
