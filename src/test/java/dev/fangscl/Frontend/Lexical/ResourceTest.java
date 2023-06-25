package dev.fangscl.Frontend.Lexical;

import dev.fangscl.Runtime.BaseTest;
import org.junit.jupiter.api.Test;

class ResourceTest extends BaseTest {

    @Test
    void varNameCollision() {
         resolve("""
                resource vm main {
                 name = "main"
                 }
                """);

    }


}