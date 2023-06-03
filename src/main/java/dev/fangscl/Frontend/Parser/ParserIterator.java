package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.errors.ParseError;
import dev.fangscl.Frontend.visitors.AstPrinter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.ListIterator;

@Log4j2
public class ParserIterator {
    private final List<Token> tokens;
    private final ListIterator<Token> iterator;
    private final AstPrinter astPrinter = new AstPrinter();

    @Getter
    @Setter
    private Token current;

    public ParserIterator(List<Token> tokens) {
        this.tokens = tokens;
        this.iterator = tokens.listIterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    boolean IsLookAhead(int k, TokenType... type) {
        var iterator = this.tokens.listIterator(this.iterator.previousIndex() + 1);
        for (var i = 0; i < k && iterator.hasNext(); i++, iterator.next()) {
            Token token = lookAhead();
            if (token == null || token.is(type)) {
                return true;
            }
        }
        return false;
    }


    boolean IsLookAheadAfter(TokenType after, TokenType... type) {
        int index = this.iterator.previousIndex() + 1;
        var iterator = this.tokens.listIterator(index);
        while (iterator.hasNext()) {
            var token = iterator.next();
            if (token.is(TokenType.EOF)) {
                break;
            }
            if (token.is(after)) {
                token = iterator.next();
                if (token.is(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean IsLookAhead(TokenType... type) {
        if (!iterator.hasNext()) {
            return false;
        }
        Token token = lookAhead();
        return token != null && token.is(type);
    }

    Token lookAhead() {
        if (!iterator.hasNext()) {
            return null;
        }
        return tokens.get(iterator.nextIndex());
    }

    Token eat(String error, TokenType... type) {
        Token lookAhead = lookAhead();
        if (lookAhead.is(TokenType.EOF)) {
            throw error(lookAhead, error);
        }
        if (!lookAhead.is(type)) {
            throw error(lookAhead, error);
        }
        return eat();
    }

    Token eat(TokenType... type) {
        return eat("Unexpected token found", type);
    }

    Token eat() {
        current = next();
        return current;
    }

    Token next() {
        return iterator.next();
    }

    Token prev() {
        return iterator.previous();
    }

    public Token eatIf(TokenType token) {
        // if the line terminator was not eaten in parsing, we consume it here. LineTerminator could be consumed by some other rules
        if (IsLookAhead(token)) {
            return eat();
        }
        return null;
    }

    public void eatLineTerminator() {
        while (IsLookAhead(TokenType.lineTerminator(), TokenType.SemiColon)) {
            eat();
        }
    }

    public void synchronize() {
        while (hasNext()) {
            var next = eat();
            if (next.isLineTerminator() || next.is(TokenType.EOF)) {
                return;
            }

            switch (next.getType()) {
                case Resource, Fun, Var, For, While, Return -> {
                    return;
                }
            }
        }
    }

    public ParseError error(Token token, String message) {
        if (token.is(TokenType.EOF)) {
            log.error("Line: " + token.getLine() + " at end. " + message);
        } else {
            log.error("Line: " + token.getLine() + " at  " + token.getRaw() + ": " + message);
        }
        return new ParseError();
    }

}
