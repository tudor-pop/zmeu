package dev.fangscl.Parsing.Lexer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.List;

/**
 * -----------------------------------------------------------
 * ------------               Lexer             --------------
 * -----------------------------------------------------------
 * ------   Responsible for producing tokens from source   ---
 * -----------------------------------------------------------
 */
@Log4j2
public class Lexer {
    @Getter
    private final LinkedList<Token> tokens = new LinkedList<>();

    public List<Token> tokenize(String source) {
        var iterator = new StringCharacterIterator(source);
        for (char i = iterator.first(); iterator.current() != CharacterIterator.DONE; i = iterator.next()) {
            if (Character.isDigit(i) || i == '.') {
                Token token = handleDigit(iterator);
                tokens.add(token);
            } else if (Character.isAlphabetic(i)) {
                var token = handleAlphabetic(iterator);
                tokens.add(token);
            } else if (TokenType.isSymbol(i)) {
                var token = new Token(i, TokenType.toSymbol(i));
                tokens.add(token);
            } else if (TokenType.isSkippable(i)) {
            } else {
                log.error("Unrecognized character was found in source: {}", i);
//                System.exit(1);
            }

        }
        return tokens;
    }

    private Token handleAlphabetic(StringCharacterIterator iterator) {
        /* parse the keyword if there are multiple digits */
        var tokenString = new StringBuilder(3);
        for (char j = iterator.current(); j != CharacterIterator.DONE; j = iterator.next()) {
            if (Character.isAlphabetic(j)) {
                tokenString.append(j);
            } else {
                iterator.previous();
                break;
            }
        }
        String keyword = tokenString.toString();
        TokenType type = TokenType.toKeyword(keyword);
        return new Token(keyword, type);
    }

    private Token handleDigit(StringCharacterIterator iterator) {
        /* parse the number if there are multiple digits */
        var tokenString = new StringBuilder(2);

        for (char j = iterator.current(); j != CharacterIterator.DONE; j = iterator.next()) {
            /* j stops at the next character after the number eg 12;
             * i must be at position of number 2 because it will get incremented to ; at the end of the top loop
             * */
            if (Character.isDigit(j)) {
                tokenString.append(j);
            } else if (j == '.') {
                // valid input: 1.2 , .5 , 0.522123f
                for (char k = j; k != CharacterIterator.DONE; k = iterator.next()) {
                    if (tokenString.length() > 0) {
                        int j2 = tokenString.charAt(tokenString.length() - 1);
                        if (j2 == 'f' || j2 == 'd') {
                            // .2dddd or 0.223ffff ... multiple f/d not allowed or just break
                            throw new LexerTokenException("Invalid decimal number");
                        }
                    }
                    tokenString.append(k); // add the "." to the number
                }
                return new Token(tokenString.toString(), TokenType.Decimal);
                // invalid input: 2.  0.d
            } else {
                return new Token(tokenString.toString(), TokenType.toNumber(iterator.previous()));
            }
        }
        return new Token(tokenString.toString(), TokenType.toNumber(iterator.previous()));
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
