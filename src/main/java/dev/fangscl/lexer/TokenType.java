package dev.fangscl.lexer;

import org.apache.commons.lang3.ArrayUtils;

// var x = 40 + (foo * bar)
// [VarToken, IdentifierToken, EqualsToken, IntegerToken]
public enum TokenType {
    Var,
    Const,
    Integer,
    Decimal,
    Equals,
    Identifier,
    OpenParanthesis,
    CloseParanthesis,
    OpenBraces,
    CloseBraces,
    OpenBrackets,
    CloseBrackets,
    BinaryOperator,
    Null,
    EOF,
    Unknown;

    public static TokenType toSymbol(char token) {
        return switch (token) {
            case '=' -> Equals;
            case '(' -> OpenParanthesis;
            case ')' -> CloseParanthesis;
            case '{' -> OpenBraces;
            case '}' -> CloseBraces;
            case '[' -> OpenBrackets;
            case ']' -> CloseBrackets;
            case '+', '-', '*', '/', '%' -> BinaryOperator;
            default -> Unknown;
        };
    }

    public static boolean in(String operator, String... symbols) {
        return ArrayUtils.contains(symbols, operator);
    }

    public static TokenType toSymbol(String token) {
        return toSymbol(token.charAt(0));
    }

    public static boolean isSymbol(char character) {
        return toSymbol(character) != Unknown;
    }

    public static TokenType toKeyword(String keyword) {
        return switch (keyword) {
            case "var"-> Var;
            case "const"-> Const;
            case "null"-> Null;
            default -> Identifier;
        };
    }

    public static boolean isKeyword(String keyword) {
        return toKeyword(keyword) != Unknown;
    }

    public static TokenType toKeywordOrIdentifier(String keyword) {
        var res = toKeyword(keyword);
        return res == Unknown ? Identifier : res;
    }

    public static TokenType toNumber(char digit) {
        if (Character.isDigit(digit)) {
            return Integer;
        }
        return Unknown;
    }

    public static boolean isSkippable(char character) {
        return Character.isWhitespace(character);
    }
}
