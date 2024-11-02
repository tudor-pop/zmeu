package io.zmeu.Engine;

import io.zmeu.Runtime.Values.SchemaValue;
import io.zmeu.TypeChecker.BaseChecker;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class EngineTest extends BaseChecker {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
        interpreter.setEngine(engine);
    }

    @Test
    void resourceDependencyIsAdded() {
        var res = eval("""
                schema vm { 
                    var name:String
                    var maxCount=0
                }
                resource vm main {
                    name = "first"
                    maxCount=1
                }
                resource vm second {
                    name = "second"
                    maxCount = vm.main.maxCount
                }
                """);
        var vm = (SchemaValue) global.lookup("vm");
        var main = vm.getInstance("main");
        assertEquals(2, engine.getResources().size());
        assertEquals(main, engine.getResources().get(0));
    }

    @Test
    void resourceDependencyReverseOrder() {
        var res = eval("""
                schema vm { 
                    var name:String
                    var maxCount=0
                }
                
                resource vm second {
                    name = "second"
                    maxCount = vm.main.maxCount
                }
                
                resource vm main {
                    name = "first"
                    maxCount=1
                }
                """);
        var vm = (SchemaValue) global.lookup("vm");
        var second = vm.getInstance("second");
        assertEquals(2, engine.getResources().size());
        assertEquals(second, engine.getResources().get(0));
    }

}