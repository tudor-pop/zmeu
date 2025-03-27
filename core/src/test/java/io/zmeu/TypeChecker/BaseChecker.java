package io.zmeu.TypeChecker;

import io.zmeu.Frontend.Parse.BaseTest;
import io.zmeu.Runtime.Environment.Environment;
import org.junit.jupiter.api.BeforeEach;

public class BaseChecker extends BaseTest {
    protected TypeChecker checker;

    @BeforeEach
    void setUp() {
        checker = new TypeChecker();
    }

    @Override
    protected Object eval(String source) {
        return checker.eval(super.src(source));
    }

    protected Environment getEnvironment() {
        return checker.getEnv();
    }
}
