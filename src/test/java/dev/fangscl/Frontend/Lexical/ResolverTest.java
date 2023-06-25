package dev.fangscl.Frontend.Lexical;

import dev.fangscl.Runtime.BaseTest;
import org.junit.jupiter.api.Test;

class ResolverTest extends BaseTest {

    @Test
    void varNameCollision() {
        var res = resolve("""
                {
                  var a = "first";
                  var a = "second";
                }
                """);

        System.out.println(res);
    }

}