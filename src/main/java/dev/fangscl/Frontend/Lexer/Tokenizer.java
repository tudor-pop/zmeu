package dev.fangscl.Frontend.Lexer;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.nio.CharBuffer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
            if (token == null || token.getType() == TokenType.WhiteSpace) {
                continue;
            }
            tokens.add(token);
        }
        return tokens;
    }

    @Nullable
    private Token getNextToken(char i) {
        CharBuffer str = source.subSequence(iterator.getIndex(), iterator.getEndIndex());
        for (var it : TokenizerSpec.spec.entrySet()) {
            var token = handle(it.getKey(), it.getValue(), str);
            if (token == null) {
                continue;
            }
            return token;
        }
        throw new TokenException("Unrecognized character was found in source: " + i);
    }

    private Token handle(Pattern pattern, TokenType type, CharBuffer str) {
        var matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return null;
        }
        var group = matcher.group();
        iterator.setIndex(iterator.getIndex() + matcher.end());
        if (type == TokenType.Number) {
            return group.contains(".") ?
                    new Token(group, TokenType.Decimal) :
                    new Token(group, TokenType.Integer);
        }

        return new Token(group, type);
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

    private boolean hasNext() {
        return this.iterator.current() != CharacterIterator.DONE;
    }

    private boolean isEOF() {
        return this.iterator.current() == CharacterIterator.DONE;
    }

}
