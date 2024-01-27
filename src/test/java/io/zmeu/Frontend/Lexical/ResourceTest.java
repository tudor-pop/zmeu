package io.zmeu.Frontend.Lexical;

import io.zmeu.Runtime.BaseTest;
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