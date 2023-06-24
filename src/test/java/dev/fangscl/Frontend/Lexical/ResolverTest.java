package dev.fangscl.Frontend.Lexical;

import dev.fangscl.Runtime.BaseTest;
import org.junit.jupiter.api.Test;

class ResolverTest extends BaseTest {

    @Test
    void varNameCollision() {
        var res = resolver.eval(parser.produceAST(tokenizer.tokenize("""
                {
                  var a = "first";
                  var a = "second";
                }
                """)));

        System.out.println(res);
    }

}