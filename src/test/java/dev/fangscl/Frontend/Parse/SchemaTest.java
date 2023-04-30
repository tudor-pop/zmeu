package dev.fangscl.Frontend.Parse;

import dev.fangscl.Frontend.Parser.Expressions.AssignmentExpression;
import dev.fangscl.Frontend.Parser.Expressions.MemberExpression;
import dev.fangscl.Frontend.Parser.Expressions.ThisExpression;
import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class SchemaTest extends BaseTest {

    @Test
    void schemaDeclaration() {
        var res = parser.produceAST(tokenizer.tokenize("""
                schema square { 
                    var x=1
                }
                """));
        var expected = Program.of(
                SchemaDeclaration.of(Identifier.of("square"),
                        BlockStatement.of(
                                VariableStatement.of(VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(1)))
                        )
                )
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void function() {
        var res = parser.produceAST(tokenizer.tokenize("""
                schema square { 
                    fun test() {
                    
                    }
                }
                """));
        var expected = Program.of(
                SchemaDeclaration.of(Identifier.of("square"),
                        BlockStatement.of(
                                FunctionDeclaration.of(Identifier.of("test"), Collections.emptyList(), BlockStatement.of())
                        )
                )
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void initSchema() {
        var res = parser.produceAST(tokenizer.tokenize("""
                schema square {
                    init() {

                    }
                }
                """));
        var expected = Program.of(
                SchemaDeclaration.of(Identifier.of("square"),
                        BlockStatement.of(
                                InitStatement.of(Collections.emptyList(), BlockStatement.of())
                        )
                )
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void initSchemaWithParams() {
        var res = parser.produceAST(tokenizer.tokenize("""
                schema square {
                    
                    init(x) {
                        this.x=x    
                    }
                }
                """));
        var expected = Program.of(
                SchemaDeclaration.of(Identifier.of("square"),
                        BlockStatement.of(
                                InitStatement.of(List.of(Identifier.of("x")),
                                        BlockStatement.of(
                                                ExpressionStatement.of(
                                                AssignmentExpression.of("=",
                                                        MemberExpression.of(false, ThisExpression.of(), "x"), "x")))
                                )
                        )
                )
        );
        log.warn(gson.toJson(res));
        assertEquals(expected, res);
    }


}
