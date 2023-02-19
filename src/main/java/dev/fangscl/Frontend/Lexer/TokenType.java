package dev.fangscl.Frontend.Lexer;

import org.apache.commons.lang3.ArrayUtils;

// var x = 40 + (foo * bar)
// [VarToken, IdentifierToken, EqualsToken, IntegerToken]
public enum TokenType {
    /*****   Literal types   *****/
    Integer,
    Decimal,
    Identifier,
    String,

    /******   Expressions   ******/
    Equals,
    Var,
    Param,
    This,


    /******   IAC   *****/
    Resource,
    Existing,
    Module,

    /******   Visibility *****/
     /* all properties are private by default. This means that:
     1. the property/module will be accessible from other files
     2. the property/module won't be logged
     3. the property/module will appear in state file/deployment history
     */
    Public,
    /* all properties are private by default. This means that:
     1. the property/module won't be accessible from other files
     2. the property/module won't be logged
     3. the property/module will appear in state file/deployment history
     */
    Private,
    /* all properties are private by default. This means that:
     1. the property/module will be accessible from other files (access some secure password)
     2. the property/module won't be logged
     3. the property/module won't appear in state file/deployment history
     */
    Secure,
    /* all properties are private by default. This means that:
     1. the property/module will be accessible from other files
     2. the property/module will be logged
     3. the property/module will appear in state file/deployment history
     */
    Output,

    /*****   Grouping   *****/
    OpenParanthesis,
    CloseParanthesis,
    OpenBraces,
    CloseBraces,
    OpenBrackets,
    CloseBrackets,

    /*****   Operators   ******/
    BinaryOperator,
    TernaryOperator,
    OptionalOperator,
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
            case '?' -> OptionalOperator;
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
            case "var" -> Var;
            case "param" -> Param;
            case "this" -> This;
            case "resource" -> Resource;
            case "public" -> Public;
            case "private" -> Private;
            case "secure" -> Secure;
            case "output" -> Output;
            case "existing" -> Existing;
            case "module" -> Module;
            case "null" -> Null;
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
