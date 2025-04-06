package io.zmeu.Base;

import io.zmeu.Diff.JaversFactory;
import io.zmeu.Engine.Repository;
import io.zmeu.TypeChecker.BaseChecker;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

public class JaversWithInterpreterTest extends BaseChecker {
    protected ModelMapper mapper;
    private AutoCloseable closeable;

    @SneakyThrows
    @BeforeEach
    void init() {
        mapper = new ModelMapper();
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

}
