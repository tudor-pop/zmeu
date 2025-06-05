package io.zmeu.Base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Config.FlywayConf;
import io.zmeu.Config.ObjectMapperConf;
import io.zmeu.Diff.JaversFactory;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.junit.jupiter.api.BeforeEach;

public class StateTest {
    protected Javers javers;
    protected ObjectMapper mapper;

    @SneakyThrows
    @BeforeEach
    void init() {
        javers = JaversFactory.createNoDb();
        mapper = ObjectMapperConf.getObjectMapper();
        FlywayConf.init();
    }
}
