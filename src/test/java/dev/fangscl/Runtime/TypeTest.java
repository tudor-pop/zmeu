package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Log4j2
public class TypeTest extends BaseTest {

    @Test
    void typeDeclaration() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    
                }
                """)));
        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals(Identifier.of("Vm"), actual.getType());
    }

    @Test
    void typeDeclarationWithFunction() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    fun test(){
                    
                    }
                }
                """)));
        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals(FunValue.of("test", actual.getEnvironment()), actual.getEnvironment().lookup("test"));
    }

    @Test
    void typeDeclarationWithVariable() {
        var res = (Identifier) interpreter.eval
                (parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    var x
                }
                """)));
        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertNull(actual.getEnvironment().get("x"));
    }

    @Test
    void typeDeclarationWithVariableInit() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    var x=20.2
                }
                """)));
        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals(20.2, actual.getEnvironment().get("x"));
    }

    @Test
    void typeDeclarationWithVariableInitString() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    var x="hello"
                }
                """)));
        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals("hello", actual.getEnvironment().get("x"));
    }

    @Test
    void initDeclaration() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    init(){
                       
                    }
                }
                """)));

        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals(FunValue.of("init", actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }
    @Test
    void initDeclarationWithParams() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    init(x){
                       
                    }
                }
                """)));

        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals(FunValue.of("init", List.of(Identifier.of("x")), actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }

    @Test
    void initDeclarationWithParamsAssignment() {
        var res = (Identifier) interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                type Vm {
                    var x = 1;
                }
                """)));

        log.warn(toJson(res));
        var actual = (TypeValue) global.get("Vm");

        assertEquals(res, actual);
    }


}
