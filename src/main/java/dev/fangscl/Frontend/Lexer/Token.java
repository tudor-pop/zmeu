package dev.fangscl.Frontend.Lexer;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class Token {
    private final Object literal;
    private final int line;
    private final String value;
    private final TokenType type;

    public Token(String value, TokenType type, Object literal, int line) {
        this.value = value;
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

    public int getValueInt() {
        return Integer.parseInt(this.value);
    }

    public double getValueDouble() {
        return Double.parseDouble(this.value);
    }

}
