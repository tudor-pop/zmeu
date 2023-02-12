package lexer;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Log4j2
public class LexerBasicsTest {

    @Test
    void checkLoop() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            for (int j = i; j < 10; j++) {
                if (j == 7) {
                    i = j + 1;
                    System.out.printf("i=%d,j=%d%n", i, j);
                    break;
                }
            }
            System.out.println(i);
        }
    }
    @Test
    void checkCharacter() {
        Assertions.assertFalse(Character.isAlphabetic('+'));
        Assertions.assertFalse(Character.isAlphabetic('-'));
        Assertions.assertFalse(Character.isAlphabetic('('));
        Assertions.assertFalse(Character.isAlphabetic(')'));
        Assertions.assertFalse(Character.isAlphabetic('*'));
        Assertions.assertFalse(Character.isAlphabetic('/'));
        Assertions.assertFalse(Character.isAlphabetic('{'));
        Assertions.assertFalse(Character.isAlphabetic('}'));
        Assertions.assertFalse(Character.isAlphabetic('-'));
        Assertions.assertFalse(Character.isAlphabetic('>'));
        Assertions.assertFalse(Character.isAlphabetic('<'));
        Assertions.assertFalse(Character.isAlphabetic('?'));
        Assertions.assertFalse(Character.isAlphabetic('!'));
        Assertions.assertFalse(Character.isAlphabetic('='));
    }
}
