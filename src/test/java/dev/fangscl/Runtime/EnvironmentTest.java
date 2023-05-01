package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
public class EnvironmentTest {
    private Environment environment;

    @BeforeEach
    void init() {
        environment = new Environment();
    }

    @Test
    void declareVar() {
        var res = environment.init("x", 1);
        var expected = IntegerValue.of(1);
        Assertions.assertEquals(expected, res);
        Assertions.assertEquals(expected, environment.get("x"));
        log.warn(res);
    }

    @Test
    void lookupVar() {
        Assertions.assertThrows(NotFoundException.class, () -> environment.lookup("y"));
    }

    @Test
    void lookupGlobalVar() {
        var global = new Environment();
        global.init("VERSION", 10);
        var interpreter = new Interpreter(global);
        Assertions.assertEquals(IntegerValue.of(10), interpreter.eval(Identifier.of("VERSION")));
    }

}
