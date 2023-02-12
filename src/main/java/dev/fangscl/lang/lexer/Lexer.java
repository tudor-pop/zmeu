package dev.fangscl.lang.lexer;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class Lexer {

    public List<Token> tokenize(String source) {
        var tokens = new ArrayList<Token>();
        for (int i = 0; i < source.length(); i++) {
            var it = source.charAt(i);

            if (Character.isDigit(it)) {
                /* parse the number if there are multiple digits */
                var tokenString = new StringBuilder(2);

                for (int j = i; j < source.length(); j++, i = j - 1) {
                    /* j stops at the next character after the number eg 12;
                    * i must be at position of number 2 because it will get incremented to ; at the end of the top loop
                    * */
                    var jit = source.charAt(j);
                    if (Character.isDigit(jit)) {
                        tokenString.append(jit);
                    } else {
                        break;
                    }
                }
                var token = new Token(tokenString.toString(), TokenType.toNumber(it));
                tokens.add(token);
            } else if (Character.isAlphabetic(it)) {
                /* parse the keyword if there are multiple digits */
                var tokenString = new StringBuilder(3);
                for (int j = i; j < source.length(); j++, i = j-1) {
                    var jit = source.charAt(j);
                    if (Character.isAlphabetic(jit)) {
                        tokenString.append(jit);
                    } else {
                        break;
                    }
                }
                String keyword = tokenString.toString();
                TokenType type = TokenType.toKeyword(keyword);
                if (type == TokenType.Unknown) {
                    var token = new Token(keyword, TokenType.Identifier);
                    tokens.add(token);
                } else {
                    var token = new Token(keyword, type);
                    tokens.add(token);
                }
            } else if (TokenType.isSymbol(it)) {
                var token = new Token(it, TokenType.toSymbol(it));
                tokens.add(token);
            } else if (TokenType.isSkippable(it)) {
            } else {
                log.error("Unrecognized character was found in source: {}", it);
//                System.exit(1);
            }
        }
        tokens.add(new Token("EOF", TokenType.EOF));
        return tokens;
    }
}
