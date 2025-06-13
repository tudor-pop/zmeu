package io.zmeu.Dummy;

import io.zmeu.Base.JaversWithInterpreterTest;
import io.zmeu.Diff.Diff;
import io.zmeu.Engine.ResourceManager;
import io.zmeu.Persistence.ResourceRepository;
import io.zmeu.Plugin.Providers;
import io.zmeu.TypeChecker.Types.ResourceType;
import io.zmeu.javers.ResourceChangeLog;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Log4j2
class DummyMultipleResourcesTest extends JaversWithInterpreterTest {
    private Diff diff;
    private Providers providers;
    private ResourceChangeLog changeProcessor;
    private ResourceManager manager;
    private ResourceRepository repository;


    @SneakyThrows
    @BeforeEach
    void init() {
        diff = new Diff(javers, providers);
        var provider = new DummyProvider();
        providers = new Providers();
        providers.putProvider(provider.schemasString(), provider);
        changeProcessor = new ResourceChangeLog(mapper);
        repository = new ResourceRepository();
        manager = new ResourceManager(providers, gson, diff, repository);
    }

    @Test
    void noChanges() {
        ResourceType res = (ResourceType) eval(providers.schemas() + """
                
                resource DummyResource main  {
                
                }
                """);

        var eval = interpreter.visit(program);
        var resources = interpreter.getResources();
        var plan = manager.plan(resources);
        Assertions.assertFalse(plan.getMergeResults().isEmpty());
    }


}