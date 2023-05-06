package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Log4j2
public class SchemaTest extends BaseTest {

    @Test
    void schemaDeclaration() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(Identifier.of("Vm"), actual.getName());
    }

    @Test
    void schemaDeclarationWithFunction() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    fun test(){
                    
                    }
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(FunValue.of("test", actual.getEnvironment()), actual.getEnvironment().lookup("test"));
    }

    @Test
    void schemaDeclarationWithVariable() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertNull(actual.getEnvironment().get("x"));
    }

    @Test
    void schemaDeclarationWithVariableInit() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x=20.2
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(DecimalValue.of(20.2), actual.getEnvironment().get("x"));
    }

    @Test
    void schemaDeclarationWithVariableInitString() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x="hello"
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(StringValue.of("hello"), actual.getEnvironment().get("x"));
    }

    @Test
    void initDeclaration() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    init(){
                       
                    }
                }
                """)));

        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(FunValue.of("init", actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }
    @Test
    void initDeclarationWithParams() {
        RuntimeValue<Identifier> res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    init(x){
                       
                    }
                }
                """)));

        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) global.get("Vm");

        assertEquals(FunValue.of("init", List.of(Identifier.of("x")), actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }


}
