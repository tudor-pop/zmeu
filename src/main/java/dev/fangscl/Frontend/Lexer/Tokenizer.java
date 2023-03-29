package dev.fangscl.Frontend.Lexer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.lang.String;
import java.nio.CharBuffer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static dev.fangscl.Frontend.Lexer.TokenType.*;
import static dev.fangscl.Frontend.Lexer.TokenizerSpec.spec;

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
    private int line = 1;
    /**
     * CharBuffer won't create a copy of the string when doing string.substring(start,end)
     */
    private CharBuffer source;


    public List<Token> tokenize(String source) {
        this.iterator = new StringCharacterIterator(source);
        this.source = CharBuffer.wrap(source);

        for (char ch = iterator.first(); hasNext(); ch = iterator.next()) {
            if (ch == '\n') {
                line++;
                continue;
            }
            var token = getNextToken(ch);
            if (token == null) {
                continue;
            }
            tokens.add(token);
        }
        tokens.add(new Token(EOF.toString(), EOF, null, line));
        return tokens;
    }

    @Nullable
    private Token getNextToken(char ch) {
        if (isAlpha(ch)) {
            return identifier();
        }

        for (var it : spec) {
            CharBuffer str = source.subSequence(iterator.getIndex(), iterator.getEndIndex());
            var value = handle(it.getPattern(), str);
            if (value == null) {
                continue;
            }
            TokenType type = it.getType();
            if (TokenType.isAny(type, WhiteSpace, Comment, NewLine)) {
                return null;
            }
            if (type == NewLine) {
                line++;
                continue;
            }
            return new Token(value, type, value, line);
        }
        log.error("{} {} | Unknown symbol: {}", line, line, ch);
        return null;
    }

    private Token identifier() {
        int start = iterator.getIndex();
        while (isAlphaNumeric(lookahead())) {
            iterator.next();
        }
        var keyword = source.subSequence(start, iterator.getIndex()).toString();
        var keywordOrId = toKeyword(keyword);
        iterator.previous();
        return new Token(keyword, keywordOrId, keyword, line);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private String handle(Pattern pattern, CharBuffer str) {
        var matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return null;
        }
        iterator.setIndex(iterator.getIndex() + matcher.end() - 1);

        return matcher.group();
    }

    private char lookahead() {
        if (isEOF()) return '\0';
        return iterator.current();
    }

    private boolean hasNext() {
        return this.iterator.current() != CharacterIterator.DONE;
    }

    private boolean isEOF() {
        return this.iterator.current() == CharacterIterator.DONE;
    }

    public Token tokenizeLiteral(String source) {
        return tokenize(source).get(0);
    }
}
