package io.zmeu.Base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zmeu.Config.FlywayConf;
import io.zmeu.Config.ObjectMapperConf;
import io.zmeu.Diff.JaversFactory;
import io.zmeu.TypeChecker.BaseChecker;
import lombok.SneakyThrows;
import org.javers.core.Javers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class JaversWithInterpreterTest extends BaseChecker {
    protected ObjectMapper mapper;
    private AutoCloseable closeable;
    protected Javers javers;

    @SneakyThrows
    @BeforeEach
    void init() {
        mapper = ObjectMapperConf.getObjectMapper();
        closeable = MockitoAnnotations.openMocks(this);
        FlywayConf.init();
        javers = JaversFactory.createNoDb();
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
