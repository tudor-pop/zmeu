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
    @Test
    void checkInteger() {
        Number number = 2;
        Assertions.assertEquals(2, number);
    }
    @Test
    void checkFloat() {
        Number number = 2.1f;
        Assertions.assertEquals(2.1f, number);
    }
    @Test
    void checkDouble() {
        Number number = 2.1d;
        Assertions.assertEquals(2.1, number);
    }
}
