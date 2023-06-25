package dev.fangscl.Frontend.Lexical;

import dev.fangscl.Frontend.Parser.errors.ParseError;
import dev.fangscl.Runtime.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResolverTest extends BaseTest {

    @Test
    void varNameCollision() {
        Assertions.assertThrows(ParseError.class, () -> resolve("""
                {
                  var a = "first";
                  var a = "second";
                }
                """)
        );
    }
}