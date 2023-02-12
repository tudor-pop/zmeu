package dev.fangscl.lang.lexer;

import lombok.Data;

@Data
public class Token {
    private final String value;
    private final TokenType type;

    public Token(String value) {
        this(value, TokenType.toSymbol(value));
    }

    public Token(char value, TokenType type) {
        this.value = String.valueOf(value);
        this.type = type;
    }

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }


}
