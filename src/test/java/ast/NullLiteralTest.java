package ast;

import dev.fangscl.Runtime.TypeSystem.Literals.NullLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NullLiteralTest extends AstLiteralTest{
    @Test
    void testAdditionParanthesis() {
        var res = parser.produceAST("null");
        Assertions.assertInstanceOf(NullLiteral.class, res.getBody().get(0));
    }

}
