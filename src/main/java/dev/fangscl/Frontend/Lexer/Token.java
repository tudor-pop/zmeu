package dev.fangscl.Frontend.Lexer;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public class Token {
    private final String value;
    private final TokenType type;

    public Token(String value) {
        this(value, TokenType.toSymbol(value));
    }

    public Token(CharSequence value, TokenType type) {
        this.value = value.toString();
        this.type = type;
    }

    public Token(char value, TokenType type) {
        this.value = String.valueOf(value);
        this.type = type;
    }

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public boolean in(String... list){
        return ArrayUtils.contains(list, this.value);
    }

    public int getValueInt() {
        return Integer.parseInt(this.value);
    }

}
