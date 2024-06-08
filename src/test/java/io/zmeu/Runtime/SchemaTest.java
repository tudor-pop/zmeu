package io.zmeu.Runtime;

import io.zmeu.Runtime.Values.FunValue;
import io.zmeu.Runtime.Values.SchemaValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.zmeu.Frontend.Parser.Literals.ParameterIdentifier.param;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class SchemaTest extends BaseTest {

    @Test
    void declare() {
        var res = eval("""
                schema Vm {
                    
                }
                """);
        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        assertEquals("Vm", actual.getType().string());
    }

    @Test
    void declareWithFunction() {
        var res = eval("""
                schema Vm {
                    fun test(){
                    
                    }
                }
                """);
        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        Assertions.assertEquals(FunValue.of("test", actual.getEnvironment()), actual.getEnvironment().lookup("test"));
    }

    @Test
    void declareWithVariable() {
        var res = eval("""
                schema Vm {
                    var x
                }
                """);
        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        assertNull(actual.getEnvironment().get("x"));
    }

    @Test
    void declareWithVariableInit() {
        var res = eval("""
                schema Vm {
                    var x=20.2
                }
                """);
        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        Assertions.assertEquals(20.2, actual.getEnvironment().get("x"));
    }

    @Test
    void declareWithVariableInitString() {
        var res = eval("""
                schema Vm {
                    var x="hello"
                }
                """);
        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        Assertions.assertEquals("hello", actual.getEnvironment().get("x"));
    }

    @Test
    void initDeclaration() {
        var res = eval("""
                schema Vm {
                    init(){
                       
                    }
                }
                """);

        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        Assertions.assertEquals(FunValue.of("init", actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }
    @Test
    void initDeclarationWithParams() {
        var res = eval("""
                schema Vm {
                    init(x){
                       
                    }
                }
                """);

        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        Assertions.assertEquals(FunValue.of("init", List.of(param("x")), actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }

    @Test
    void initDeclarationWithParamsAssignment() {
        var res = eval("""
                schema Vm {
                    var x = 1;
                }
                """);

        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        assertEquals(res, actual);
    }

    @Test
    void initDeclarationWithPathType() {
        var res = eval("""
                schema Vm {
                    var x:Number = 1;
                }
                """);

        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        assertNotNull(res);
        assertEquals(res, actual);
    }

    @Test
    void initDeclarationWithWrontInit() {
        var res = eval("""
                schema Vm {
                    var x:Number = "test";
                }
                """);

        log.warn(toJson(res));
        var actual = (SchemaValue) global.get("Vm");

        assertNull(res);
        assertEquals(res, actual);
    }


}
