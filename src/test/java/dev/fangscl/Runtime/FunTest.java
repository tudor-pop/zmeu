package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.VariableStatement;
import dev.fangscl.Runtime.Values.FunValue;
import dev.fangscl.Runtime.exceptions.VarExistsException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class FunTest extends BaseTest {

    @Test
    void funDeclaration() {
        var res = (FunValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun myFun(){
                    var x = 1
                }
                """)));
        var expected = FunValue.of(
                Identifier.of("myFun"),
                List.of(),
                ExpressionStatement.of(BlockExpression.of(VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(1))))),
                global
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
        assertEquals(global.get("myFun"), res);
    }

    @Test
    void funReturn() {
        var res = (FunValue) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun myFun(){
                   var x = 1
                   x
                }
                """)));
        var expected = FunValue.of(
                Identifier.of("myFun"),
                List.of(),
                ExpressionStatement.of(BlockExpression.of(VariableStatement.of(
                                VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(1))
                        ),
                        ExpressionStatement.of(Identifier.of("x")))),
                global
        );

        log.warn(toJson(res));
        assertEquals(expected, res);
        assertEquals(global.get("myFun"), res);
    }

    @Test
    void funEvaluateBlock() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun myFun(x){
                   x
                }
                myFun(2)
                """)));
        log.warn(toJson(res));
        assertEquals(2, res);
    }
    @Test
    void funEvaluateBlockWithOuter() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                var x = "global"
                {
                    fun myFun(){
                       var y=1
                       print(x)
                    }
                    myFun()
                    var x="local"
                    myFun()
                    
                }
                """)));
        log.warn(toJson(res));
        assertEquals("global", res);
    }

    @Test
    void funBody() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun sqrt(x){
                   x*x
                }
                sqrt(2)
                """)));
        log.warn(toJson(res));
        assertEquals(4, res);
    }

    @Test
    void funBodyOverlappingWithParam() {
        Assertions.assertThrows(VarExistsException.class, () -> {
            interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                    fun sqrt(x){
                       var x = 2
                       x*x
                    }
                    sqrt(2)
                    """)));
        });
    }

    @Test
    void funBodyMultiParams() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun sqrt(x,y){
                   var z = 1
                   x*y+z
                }
                sqrt(2,3)
                """)));
        log.warn(toJson(res));
        assertEquals(7, res);
    }

    @Test
    void funClojure() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
                """)));
        log.warn(toJson(res));
        assertEquals(160, res);
    }

    @Test
    void returnStatement() {
        var res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun fib(n) {
                   if (n <= 1) {
                        return n
                   }
                   return fib(n - 2) + fib(n - 1)
                 }
                var x = fib(6)
                println("fib result is: ", x)
                x
                    """)));
        log.warn(toJson(res));
        assertEquals(8, res);
    }


}
