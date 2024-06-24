package io.zmeu.Frontend.Parser.Types;

import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.TokenType;
import io.zmeu.Frontend.Parser.Literals.TypeIdentifier;
import io.zmeu.Frontend.Parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.zmeu.Frontend.Lexer.TokenType.*;
import static io.zmeu.Frontend.Lexer.TokenType.Colon;

public class TypeParser {

    private final Parser parser;

    public TypeParser(Parser parser) {
        this.parser = parser;
    }

    public List<Type> OptParameterList() {
        return IsLookAhead(CloseParenthesis, Colon) ? Collections.emptyList() : ParameterList();
    }

    private List<Type> ParameterList() {
        var params = new ArrayList<Type>();
        do {
            params.add(FunType());
        } while (IsLookAhead(Comma, Colon) && eat(Comma) != null);

        return params;
    }

    private Type FunType() {
        if (IsLookAhead(Colon)) {
            var type = TypeDeclaration();
            return type.getType();
        } else if (IsLookAhead(Identifier)) {
            var type = parser.TypeIdentifier();
            return type.getType();
        }
        return null;
    }

    public TypeIdentifier FunctionType() {
        if (parser.IsLookAhead(Colon)) {
            parser.eat(Colon);
            if (parser.IsLookAhead(OpenParenthesis)) {
                parser.eat(OpenParenthesis, "Expected '(' but got: " + parser.lookAhead());
                var params = OptParameterList();  // optimisation idea: why create ParameterIdentifier then extract just the type
                parser.eat(CloseParenthesis, "Expected ')' but got: " + parser.lookAhead());
                parser.eat(Lambda, "Expected -> but got: " + parser.lookAhead());
                var returnType = parser.TypeIdentifier();

                var type = new FunType(params, returnType.getType());
                return TypeIdentifier.builder()
                        .type(type)
                        .build();
            } else {
                return parser.TypeIdentifier();
            }
        }
        return null;
    }

    private TypeIdentifier TypeDeclaration() {
        return parser.TypeDeclaration();
    }

    private Object eat(TokenType tokenType) {
        return parser.eat(tokenType);
    }

    private boolean IsLookAhead(TokenType... tokenType) {
        return parser.IsLookAhead(tokenType);
    }

    private Token lookahead() {
        return parser.lookAhead();
    }
}
