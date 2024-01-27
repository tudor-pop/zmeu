package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.AssignmentExpression;
import io.zmeu.Frontend.Parser.Expressions.MemberExpression;
import io.zmeu.Frontend.Parser.Expressions.ThisExpression;
import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Program;
import dev.fangscl.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.Parser.Statements.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
public class SchemaTest extends BaseTest {

    @Test
    void typeDeclaration() {
        var res = parse("""
                type square { 
                    var x=1
                }
                """);
        var expected = Program.of(
                TypeDeclaration.of(Identifier.of("square"),
                        BlockExpression.of(
                                VariableStatement.of(VariableDeclaration.of(Identifier.of("x"), NumericLiteral.of(1)))
                        )
                )
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void function() {
        var res = parse("""
                type square { 
                    fun test() {
                    
                    }
                }
                """);
        var expected = Program.of(
                TypeDeclaration.of(Identifier.of("square"),
                        BlockExpression.of(
                                FunctionDeclaration.of(Identifier.of("test"), Collections.emptyList(), BlockExpression.of())
                        )
                )
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void initSchema() {
        var res = parse("""
                type square {
                    init() {

                    }
                }
                """);
        var expected = Program.of(
                TypeDeclaration.of(Identifier.of("square"),
                        BlockExpression.of(
                                InitStatement.of(Collections.emptyList(), BlockExpression.of())
                        )
                )
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
    }

    @Test
    void initSchemaWithParams() {
        var res = parse("""
                type square {
                    
                    init(x) {
                        this.x=x    
                    }
                }
                """);
        var expected = Program.of(
                TypeDeclaration.of(Identifier.of("square"),
                        BlockExpression.of(
                                InitStatement.of(List.of(Identifier.of("x")),
                                        BlockExpression.of(
                                                ExpressionStatement.of(
                                                AssignmentExpression.of("=",
                                                        MemberExpression.of(false, ThisExpression.of(), "x"), "x")))
                                )
                        )
                )
        );
        log.warn(toJson(res));
        assertEquals(expected, res);
    }


}
