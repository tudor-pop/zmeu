package io.zmeu.Base;

import io.zmeu.Diff.JaversFactory;
import io.zmeu.TypeChecker.BaseChecker;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;

public class JaversWithInterpreterTest extends BaseChecker {
    protected Javers javers;
    protected ModelMapper mapper;

    @SneakyThrows
    @BeforeEach
    void init() {
        javers = JaversFactory.createNoDb();
        mapper = new ModelMapper();
    }
}
