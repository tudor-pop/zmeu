package io.zmeu.Frontend.TypeChecker;

import org.junit.jupiter.api.BeforeEach;

public class BaseChecker {
    protected TypeChecker checker;

    @BeforeEach
    void setUp() {
        checker = new TypeChecker();
    }
}
