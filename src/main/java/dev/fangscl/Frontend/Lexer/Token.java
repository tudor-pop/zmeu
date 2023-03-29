package dev.fangscl.Frontend.Lexer;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Data
public class Token {
    private final Object literal;
    private final int line;
    private final Object value;
    private final TokenType type;

    public Token(Object value, TokenType type, Object literal, int line) {
        this.value = switch (type) {
            case Number -> NumberUtils.createNumber(value.toString());
            default -> value;
        };
        this.type = type;
        this.line = line;
        this.literal = literal;
    }

    public Token(char value, TokenType type, Object literal, int line) {
        this(String.valueOf(value), type, literal, line);
    }

    public boolean in(String... list) {
        return ArrayUtils.contains(list, this.value);
    }


}
