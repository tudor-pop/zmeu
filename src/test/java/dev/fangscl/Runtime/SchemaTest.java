package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Runtime.Values.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class SchemaTest extends BaseTest {

    @Test
    void schemaDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) environment.get("Vm");

        assertEquals(Identifier.of("Vm"), actual.getName());
    }

    @Test
    void schemaDeclarationWithFunction() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    fun test(){
                    
                    }
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) environment.get("Vm");

        assertEquals(FunValue.of("test", actual.getEnvironment()), actual.getEnvironment().lookup("test"));
    }

    @Test
    void schemaDeclarationWithVariable() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) environment.get("Vm");

        assertEquals(NullValue.of(), actual.getEnvironment().get("x"));
    }

    @Test
    void schemaDeclarationWithVariableInit() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x=20.2
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) environment.get("Vm");

        assertEquals(DecimalValue.of(20.2), actual.getEnvironment().get("x"));
    }

    @Test
    void schemaDeclarationWithVariableInitString() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x="hello"
                }
                """)));
        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) environment.get("Vm");

        assertEquals(StringValue.of("hello"), actual.getEnvironment().get("x"));
    }

    @Test
    void initDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    init(){
                       
                    }
                }
                """)));

        log.warn(gson.toJson(res));
        SchemaValue actual = (SchemaValue) environment.get("Vm");

        assertEquals(FunValue.of("init", actual.getEnvironment()), actual.getEnvironment().lookup("init"));
    }


}
