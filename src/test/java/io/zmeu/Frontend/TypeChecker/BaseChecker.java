package io.zmeu.Frontend.TypeChecker;

import io.zmeu.Frontend.Parse.BaseTest;
import org.junit.jupiter.api.BeforeEach;

public class BaseChecker extends BaseTest {
    protected TypeChecker checker;

    @BeforeEach
    void setUp() {
        checker = new TypeChecker();
    }
}