package dev.fangscl.lexer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Lexer {
    @Getter
    private final List<Token> tokens = new ArrayList<>();

    public List<Token> tokenize(String source) {
        for (int i = 0; i < source.length(); i++) {
            var it = source.charAt(i);

            if (Character.isDigit(it) || it == '.') {
                /* parse the number if there are multiple digits */
                var tokenString = new StringBuilder(2);

                for (int j = i; j < source.length(); j++, i = j - 1) {
                    /* j stops at the next character after the number eg 12;
                     * i must be at position of number 2 because it will get incremented to ; at the end of the top loop
                     * */
                    var jit = source.charAt(j);
                    if (Character.isDigit(jit)) {
                        tokenString.append(jit);
                    } else if (jit == '.') {
                        // valid input: 1.2 , .5 , 0.522123f
                        int j2 = j + 1;
                        for (int k = j; k < source.length() && (Character.isDigit(source.charAt(j2)) || source.charAt(j2) == 'f' || source.charAt(j2) == 'd'); k++, j = k - 1) {
                            var kit = source.charAt(k);
                            if (source.charAt(k) == source.charAt(j) && (source.charAt(k) == 'f' || source.charAt(k) == 'd')) {
                                // .2dddd or 0.223ffff ... multiple f/d not allowed or just break
                                throw new LexerTokenException("Invalid decimal number");
                            } else {
                                tokenString.append(kit); // add the "." to the number
                            }
                        }
                        var token = new Token(tokenString.toString(), TokenType.Decimal);
                        tokens.add(token);
                        // invalid input: 2.  0.d
                    } else {
                        break;
                    }
                }
                var token = new Token(tokenString.toString(), TokenType.toNumber(it));
                tokens.add(token);
            } else if (Character.isAlphabetic(it)) {
                /* parse the keyword if there are multiple digits */
                var tokenString = new StringBuilder(3);
                for (int j = i; j < source.length(); j++, i = j - 1) {
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
        return tokens;
    }

    public List<Token> tokenize(File src) throws IOException {
        var lines = Files.readAllLines(Paths.get(src.toURI()));
        for (var line : lines) {
            tokenize(line);
        }
        tokens.add(new Token("EOF", TokenType.EOF));
        return tokens;
    }
}
