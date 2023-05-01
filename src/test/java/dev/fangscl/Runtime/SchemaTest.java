package dev.fangscl.Runtime;

import dev.fangscl.Frontend.Parser.Expressions.AssignmentExpression;
import dev.fangscl.Frontend.Parser.Expressions.MemberExpression;
import dev.fangscl.Frontend.Parser.Expressions.ThisExpression;
import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Runtime.Values.RuntimeValue;
import dev.fangscl.Runtime.Values.SchemaValue;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class SchemaTest extends BaseTest {

    @Test
    void schemaDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    fun test(){
                    
                    }
                }
                """)));
        var expected = SchemaValue.of(
                Identifier.of("Vm"),
                BlockStatement.of(FunctionDeclaration.of("test")),
                environment
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
        assertEquals(environment.get("Vm"), res);
    }

    @Test
    void schemaDeclarationWithVar() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x
                }
                """)));
        var expected = SchemaValue.of(
                Identifier.of("Vm"),
                BlockStatement.of(VariableStatement.of(VariableDeclaration.of("x"))),
                environment
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
        assertEquals(environment.get("Vm"), res);
    }

    @Test
    void initDeclaration() {
        RuntimeValue res = interpreter.eval(parser.produceAST(tokenizer.tokenize("""
                schema Vm {
                    var x
                    init(x){
                        this.x = x
                    }
                }
                """)));
        var expected = SchemaValue.of(
                Identifier.of("Vm"),
                BlockStatement.of(VariableStatement.of(VariableDeclaration.of("x")),
                        InitStatement.of(List.of(Identifier.of("x")),
                                BlockStatement.of(
                                        ExpressionStatement.of(AssignmentExpression.of("=",
                                                MemberExpression.of(false, ThisExpression.of(),"x"),"x"))
                                ))),
                environment
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
        assertEquals(environment.get("Vm"), res);
    }


}
