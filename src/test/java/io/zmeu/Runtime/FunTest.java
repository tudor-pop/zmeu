package io.zmeu.Runtime;

import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumberLiteral;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.ExpressionStatement;
import io.zmeu.Frontend.Parser.Statements.VariableStatement;
import io.zmeu.Runtime.Values.FunValue;
import io.zmeu.Runtime.exceptions.VarExistsException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class FunTest extends BaseTest {

    @Test
    void funDeclaration() {
        var res = (FunValue) eval("""
                fun myFun(){
                    var x = 1
                }
                """);
        var expected = FunValue.of(
                Identifier.id("myFun"),
                List.of(),
                ExpressionStatement.expressionStatement(BlockExpression.block(VariableStatement.of(
                        VariableDeclaration.of(Identifier.id("x"), NumberLiteral.of(1))))),
                global
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
        Assertions.assertEquals(global.get("myFun"), res);
    }

    @Test
    void funReturn() {
        var res = (FunValue) eval("""
                fun myFun(){
                   var x = 1
                   x
                }
                """);
        var expected = FunValue.of(
                Identifier.id("myFun"),
                List.of(),
                ExpressionStatement.expressionStatement(BlockExpression.block(VariableStatement.of(
                                VariableDeclaration.of(Identifier.id("x"), NumberLiteral.of(1))
                        ),
                        ExpressionStatement.expressionStatement(Identifier.id("x")))),
                global
        );

        log.warn(toJson(res));
        assertEquals(expected, res);
        Assertions.assertEquals(global.get("myFun"), res);
    }

    @Test
    void funEvaluateBlock() {
        var res = eval("""
                fun myFun(x){
                   x
                }
                myFun(2)
                """);
        log.warn(toJson(res));
        assertEquals(2, res);
    }
    @Test
    void funEvaluateBlockWithOuter() {
        var res = eval("""
                var x = "global"
                {
                    fun myFun(){
                      println(x)
                      x
                    }
                    myFun()
                    var x="local"
                    myFun()
                    
                }
                """);
        log.warn(toJson(res));
        assertEquals("global", res);
    }

    @Test
    void funBody() {
        var res = eval("""
                fun sqrt(x){
                   x*x
                }
                sqrt(2)
                """);
        log.warn(toJson(res));
        assertEquals(4, res);
    }

    @Test
    void funBodyOverlappingWithParam() {
        Assertions.assertThrows(VarExistsException.class, () -> interpret("""
                fun sqrt(x){
                   var x = 3
                   x*x
                }
                sqrt(2)
                """));
    }

    @Test
    void funBodyMultiParams() {
        var res = eval("""
                fun sqrt(x,y){
                   var z = 1
                   x*y+z
                }
                sqrt(2,3)
                """);
        log.warn(toJson(res));
        assertEquals(7, res);
    }

    @Test
    void funClojure() {
        var res = eval("""
                {
                    var a = 100
                    fun calc(x,y){
                        var z = x+y
                        fun inner(b){
                            b+z+a
                        }
                        inner
                    }
                    var fn = calc(10,20)
                    fn(30)
                }
                """);
        log.warn(toJson(res));
        assertEquals(160, res);
    }

    @Test
    void returnStatement() {
        var res = eval("""
                fun fib(n) {
                   if (n <= 1) {
                        return n
                   }
                   return fib(n - 2) + fib(n - 1)
                }
                var x = fib(6)
                println("fib result is: ", x)
                x
                    """);
        log.warn(toJson(res));
        assertEquals(8, res);
    }


}
