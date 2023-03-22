package dev.fangscl.Frontend.Lexer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * ---------------------------------------------------------------------
 * ------------               Lexer/Tokenizer             --------------
 * ---------------------------------------------------------------------
 * ------   Responsible for producing tokens from source   -------------
 * ---------------------------------------------------------------------
 */
@Log4j2
public class Tokenizer {
    @Getter
    private final List<Token> tokens = new ArrayList<>();
    private StringCharacterIterator iterator;
    /**
     * CharBuffer won't create a copy of the string when doing string.substring(start,end)
     */
    private CharBuffer source;

    public List<Token> tokenize(String source) {
        this.iterator = new StringCharacterIterator(source);
        this.source = CharBuffer.wrap(source);

        for (char i = iterator.first(); hasNext(); i = iterator.next()) {
            var token = getNextToken(i);
            if (token == null) {
                continue;
            }
            tokens.add(token);
        }
        return tokens;
    }

    @Nullable
    private Token getNextToken(char i) {
        if (Character.isDigit(i) || i == '.') {
            return handleDigit();
        } else if (Character.isAlphabetic(i)) {
            return handleAlphabetic();
        } else if (i == '\"' || i == '\'') {
            return handle(TokenType.String);
        } else if (TokenType.isSymbol(i)) {
            return new Token(i, TokenType.toSymbol(i));
        } else if (TokenType.isSkippable(i)) {
            return null;
        }
        throw new TokenException("Unrecognized character was found in source: " + i);
    }

    private Token handle(TokenType type) {
        var str = source.subSequence(iterator.getIndex(), iterator.getEndIndex());
        var p = TokenizerSpec.spec.get(type)
                .matcher(str);
        if (p.find()) {
            return new Token(p.group(), type);
        }
        return new Token(str, TokenType.Unknown);
    }

    private Token handleAlphabetic() {
        /* parse the keyword if there are multiple digits */
        var tokenString = new StringBuilder(3);
        for (char i = iterator.current(); hasNext(); i = iterator.next()) {
            if (Character.isAlphabetic(i)) {
                tokenString.append(i);
            } else {
                iterator.previous();
                break;
            }
        }
        String keyword = tokenString.toString();
        TokenType type = TokenType.toKeyword(keyword);
        return new Token(keyword, type);
    }

    private Token handleDigit() {
//        int index = iterator.getIndex();
//        if (Character.isDigit(iterator.current())) {
//            String substring = source.substring(index);
//            Matcher matcher = Regex.isNumber(substring);
//            // number has max 7 chars. This is an optimisation because in the tutorial gets the entire substring from current index to the end
//            // which could be very costly when using many files. Assuming 7 chars long digits is a good heuristic as nobody
//            // will create 1.000.000 VMs
//            int end = index + matcher.end();
//            if (substring.contains(".")) {
//                iterator.setIndex(end);
//                return new Token(matcher.group(), TokenType.Decimal);
//            } else {
//                iterator.setIndex(end);
//                return new Token(matcher.group(), TokenType.Integer);
//            }
//        } else {
//            iterator.setIndex(index + 1);
//        }
        /* parse the number if there are multiple digits */
        var res = new StringBuilder(2);

        for (char i = iterator.current(); hasNext(); i = iterator.next()) {
            /* i stops at the next character after the number eg 12;
             * i must be at position of number 2 because it will get incremented to ; at the end of the top loop
             * */
            if (Character.isDigit(i)) {
                res.append(i);
            } else if (i == '.') {
                res.append(i); // add the "." to the number
                // valid input: 1.2 , .5 , 0.522123
                for (char j = iterator.next(); j != CharacterIterator.DONE && Character.isDigit(j); j = iterator.next()) {
//                    if (res.length() > 0) {
//                        int j2 = res.charAt(res.length() - 1);
//                        if (j2 == 'f' || j2 == 'd') {
//                            // .2dddd or 0.223ffff ... multiple f/d not allowed or just break
//                            throw new LexerTokenException("Invalid decimal number");
//                        }
//                    }

                    res.append(j);
                }
                return new Token(res.toString(), TokenType.Decimal);
                // invalid input: 2.  0.d
            } else {
                return new Token(res.toString(), TokenType.toNumber(iterator.previous()));
            }
        }
        return new Token(res.toString(), TokenType.toNumber(iterator.previous()));
    }

    public List<Token> tokenize(File src) throws IOException {
        var lines = Files.readAllLines(Paths.get(src.toURI()));
        for (var line : lines) {
            tokenize(line);
        }
        tokens.add(new Token("EOF", TokenType.EOF));
        return tokens;
    }

    private boolean hasNext() {
        return this.iterator.current() != CharacterIterator.DONE;
    }

    private boolean isEOF() {
        return this.iterator.current() == CharacterIterator.DONE;
    }

}
