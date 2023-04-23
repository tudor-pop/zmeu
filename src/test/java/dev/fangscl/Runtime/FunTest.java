package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Statements.BlockExpression;
import dev.fangscl.Frontend.Parser.Statements.ExpressionStatement;
import dev.fangscl.Frontend.Parser.Statements.VariableStatement;
import dev.fangscl.Runtime.Values.FunValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.RuntimeValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class FunTest extends BaseTest {

    @Test
    void funDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun myFun(){
                    var x = 1
                }
                """)));
        var expected = FunValue.of(Identifier.of("myFun"), BlockExpression.of(VariableStatement.of(
                VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(1)
                ))));
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
        assertEquals(environment.get("myFun"), res);
    }

    @Test
    void funReturn() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun myFun(){
                   var x = 1
                   x
                }
                """)));
        var expected = FunValue.of(Identifier.of("myFun"), BlockExpression.of(
                VariableStatement.of(
                        VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(1))
                ),
                ExpressionStatement.of(Identifier.of("x"))
        ));
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
        assertEquals(environment.get("myFun"), res);
    }

    @Test
    void funEvaluateBlock() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun myFun(x){
                   x
                }
                myFun(2)
                """)));
        var expected = IntegerValue.of(2);
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void funBody() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun sqrt(x){
                   x*x
                }
                sqrt(2)
                """)));
        var expected = IntegerValue.of(4);
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void funBodyMultiParams() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                fun sqrt(x,y){
                   var z = 1
                   x*y+z
                }
                sqrt(2,3)
                """)));
        var expected = IntegerValue.of(7);
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }
    @Test
    void funClojure() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
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
        var expected = IntegerValue.of(160);
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }


}
