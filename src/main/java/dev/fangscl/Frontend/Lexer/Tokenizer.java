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
    private int line = 1;
    /**
     * CharBuffer won't create a copy of the string when doing string.substring(start,end)
     */
    private CharBuffer source;


    public List<Token> tokenize(String source) {
        this.iterator = new StringCharacterIterator(source);
        this.source = CharBuffer.wrap(source);

        for (; hasNext(); iterator.next()) {
            var token = getNextToken();
            if (token == null || token.getType() == TokenType.WhiteSpace) {
                continue;
            }
            tokens.add(token);
        }
        tokens.add(new Token(TokenType.EOF.toString(), TokenType.EOF, null, line));
        return tokens;
    }

    @Nullable
    private Token getNextToken() {
        for (var it : TokenizerSpec.spec.entrySet()) {
            CharBuffer str = source.subSequence(iterator.getIndex(), iterator.getEndIndex());
            var value = handle(it.getKey(), str);
            if (value == null) {
                continue;
            }
            TokenType type = it.getValue();
            if (type == TokenType.WhiteSpace) {
                return null;
            }
            if (type == TokenType.NewLine) {
                line++;
                continue;
            }
            return new Token(value, type, value, line);
        }
        throw new TokenException(line, "");
    }

    private String handle(Pattern pattern, CharBuffer str) {
        var matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return null;
        }
        iterator.setIndex(iterator.getIndex() + matcher.end() - 1);

        return matcher.group();
    }

    private boolean hasNext() {
        return this.iterator.current() != CharacterIterator.DONE;
    }

    private boolean isEOF() {
        return this.iterator.current() == CharacterIterator.DONE;
    }

}
