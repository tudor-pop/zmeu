package io.zmeu.Base;

import io.zmeu.Diff.JaversFactory;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.junit.jupiter.api.BeforeEach;
import org.modelmapper.ModelMapper;

public class JaversTest {
    protected Javers javers;
    protected ModelMapper mapper;

    @SneakyThrows
    @BeforeEach
    void init() {
        javers = JaversFactory.createNoDb();
        mapper = new ModelMapper();
    }
}
