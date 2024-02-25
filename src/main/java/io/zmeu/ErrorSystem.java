package io.zmeu;

import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.TokenType;
import io.zmeu.Frontend.Parser.errors.ErrorList;
import io.zmeu.Frontend.Parser.errors.ParseError;
import io.zmeu.Runtime.exceptions.RuntimeError;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ErrorSystem {
    @Getter
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    @Getter
    private static final List<ParseError> errors = new ArrayList<>();

    public static void runtimeError(RuntimeError error) {
        System.err.printf("%s\n[line %d]%n", error.getMessage(), error.getToken().getLine());
        hadRuntimeError = true;
    }

    public static ParseError error(String message, Token token, TokenType type) {
        if (token.is(TokenType.EOF)) {
            log.error("Line: " + token.getLine() + " at end. " + message);
        } else {
            log.error("Line: " + token.getLine() + " at  " + token.getRaw() + ": " + message);
        }
        hadError = true;
        ParseError parseError = ParseError.builder()
                .actual(token)
                .message(message)
                .expected(type)
                .build();
        errors.add(parseError);
        return parseError;
    }

    public static ParseError error(String message) {
        hadError = true;
        ParseError parseError = ParseError.builder()
                .message(message)
                .build();
        errors.add(parseError);
        return parseError;
    }

    public static ErrorList error(String message, Token tokens, TokenType... type) {
        var list = new ArrayList<ParseError>(type.length);
        for (var it : type) {
            list.add(error(message, tokens, it));
        }
        return ErrorList.builder().errors(list).build();
    }

    public static void clear() {
        errors.clear();
    }

    public static String errors() {
        return errors.stream().map(ParseError::getMessage).collect(Collectors.joining("\n"));
    }

}
