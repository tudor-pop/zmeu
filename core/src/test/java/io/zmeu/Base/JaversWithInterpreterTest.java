package io.zmeu.Base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.TypeChecker.BaseChecker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class JaversWithInterpreterTest extends BaseChecker {
    protected ObjectMapper mapper;
    private AutoCloseable closeable;

    @SneakyThrows
    @BeforeEach
    void init() {
        mapper = new ObjectMapper();
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    protected Object visit(String source) {
        program = super.src(source);
        checker.visit(program);
        return interpreter.visit(program);
    }
}
