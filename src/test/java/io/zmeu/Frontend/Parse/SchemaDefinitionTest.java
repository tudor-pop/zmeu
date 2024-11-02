package io.zmeu.Frontend.Parse;

import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.TypeIdentifier;
import io.zmeu.Frontend.Parser.Program;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.zmeu.Frontend.Parser.Factory.*;
import static io.zmeu.Frontend.Parser.Literals.Identifier.id;
import static io.zmeu.Frontend.Parser.Literals.NumberLiteral.number;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@DisplayName("Parser Schema")
public class SchemaDefinitionTest extends BaseTest {

    @Test
    void schemaDeclaration() {
        var actual = (Program) parse("""
                schema square { 
                    var x:Number=1
                }
                """);
        var expected = program(
                schema(id("square"), block(
                        VariableDeclaration.var(id("x"), TypeIdentifier.type("Number"), number(1)))
                )
        );
        log.warn(toJson(actual));
        assertEquals(toJson(expected), toJson(actual));
    }

}
