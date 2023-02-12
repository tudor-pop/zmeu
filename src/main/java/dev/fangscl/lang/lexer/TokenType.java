package dev.fangscl.lang.lexer;

import java.security.Key;
import java.util.HashMap;

// var x = 40 + (foo * bar)
// [VarToken, IdentifierToken, EqualsToken, IntegerToken]
public enum TokenType {
    Var,
    Const,
    Number,
    Equals,
    Identifier,
    OpenParanthesis,
    CloseParanthesis,
    BinaryOperator,
    Unknown;

    public static TokenType toSymbol(char token) {
        return switch (token) {
            case '=' -> Equals;
            case '(' -> OpenParanthesis;
            case ')' -> CloseParanthesis;
            case '+', '-', '*', '/' -> BinaryOperator;
            default -> Unknown;
        };
    }


    public static TokenType toSymbol(String token) {
        return toSymbol(token.charAt(0));
    }

    public static boolean isSymbol(char character) {
        return toSymbol(character) != Unknown;
    }

    public static boolean isKeyword(String keyword) {
        return switch (keyword) {
            case "var", "const" -> true;
            default -> false;
        };
    }

    public static TokenType toKeyword(String keyword) {
        return switch (keyword) {
            case "var"-> Var;
            case "const"-> Const;
            default -> Unknown;
        };
    }
    public static TokenType toKeywordOrIdentifier(String keyword) {
        var res = toKeyword(keyword);
        return res == Unknown ? Identifier : res;
    }

    public static TokenType toNumber(char digit) {
        if (Character.isDigit(digit)) {
            return Number;
        }
        return Unknown;
    }

    public static boolean isSkippable(char character) {
        return Character.isWhitespace(character);
    }
}
