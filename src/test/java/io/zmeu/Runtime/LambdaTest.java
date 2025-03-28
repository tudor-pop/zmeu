package io.zmeu.Runtime;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LambdaTest extends BaseRuntimeTest {

    @Test
    void funDeclaration() {
        Object res = eval("""
                {
                    fun onClick(callback){
                        var x = 10
                        var y = 20
                        callback(x+y)
                    }
                    onClick((data)->data*10)
                }""");

        log.warn(toJson(res));
        assertEquals(300, res);
    }

    @Test
    void lambdaAssignToVar() {
        Object res = eval("""
                var f = (x) -> x*x
                f(2)
                """);

        log.warn(toJson(res));
        assertEquals(4, res);
    }

    @Test
    void lambdaInvoke() {
        Object res = eval("""
                ((x) -> x*x) (2)
                """);

        log.warn(toJson(res));
        assertEquals(4, res);
    }

    @Test
    void lambdaInvokeClojure() {
        Object res = eval("""
                {
                var y = 3
                ((x) ->{ 
                    var z=3 
                    x*y+z
                    }) (2)
                }""");

        log.warn(toJson(res));
        assertEquals(9, res);
    }

    @Test
    void lambdaInvokeClojure2() {
        Object res = eval("""
                {
                var y = 3
                ((x) ->{ 
                    var z=3 
                    var y=4
                    x*y+z
                    }) (2)
                }""");

        log.warn(toJson(res));
        assertEquals(11, res);
    }

    @Test
    void lambdaInvokeClojureWithingFunction() {
        Object res = eval("""
                                
                var y = 3
                fun foo(a) {
                    var z=3
                    (x) -> {
                        var y=4
                        x*y+z+a
                    }
                }
                var cloj = foo(2)
                cloj(1)
                                
                """);

        log.warn(toJson(res));
        assertEquals(9, res);
    }

    @Test
    void lambdaInvokeStaticClojure() {
        Object res = eval("""
                                
                var x = 10
                fun foo(){ x }
                fun bar() {
                    var x=20
                    foo() + x
                    
                }
                bar()
                """);

        log.warn(toJson(res));
        assertEquals(30, res);
    }

}
