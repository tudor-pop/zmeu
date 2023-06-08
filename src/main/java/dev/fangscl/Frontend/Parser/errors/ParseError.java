package dev.fangscl.Frontend.Parser.errors;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class ParseError extends RuntimeException{
    private Token actual;
    private TokenType expected;
    private String message;
}
