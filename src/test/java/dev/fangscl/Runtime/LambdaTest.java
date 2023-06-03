package dev.fangscl.Runtime;

import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class LambdaTest extends BaseTest {

    @Test
    void funDeclaration() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                    fun onClick(callback){
                        var x = 10
                        var y = 20
                        callback(x+y)
                    }
                    onClick((data)->data*10)
                }""")));

        var expected = IntegerValue.of(300);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void lambdaAssignToVar() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var f = (x) -> x*x
                f(2)
                """)));

        var expected = IntegerValue.of(4);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void lambdaInvoke() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                ((x) -> x*x) (2)
                """)));

        var expected = IntegerValue.of(4);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }
    @Test
    void lambdaInvokeClojure() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                var y = 3
                ((x) ->{ 
                    var z=3 
                    x*y+z
                    }) (2)
                }""")));

        var expected = IntegerValue.of(9);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }
    @Test
    void lambdaInvokeClojure2() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                {
                var y = 3
                ((x) ->{ 
                    var z=3 
                    var y=4
                    x*y+z
                    }) (2)
                }""")));

        var expected = IntegerValue.of(11);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void lambdaInvokeClojureWithingFunction() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                
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
                
                """)));

        var expected = IntegerValue.of(9);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }
    @Test
    void lambdaInvokeStaticClojure() {
        RuntimeValue res = (RuntimeValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                
                var x = 10
                fun foo(){ x }
                fun bar() {
                    var x=20
                    foo() + x
                    
                }
                bar()
                """)));

        var expected = IntegerValue.of(30);
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

}
