package dev.fangscl.Frontend.Lexer;

public class LexerTokenException extends RuntimeException {

    public LexerTokenException() {
    }

    public LexerTokenException(String message) {
        super(message);
    }

    public LexerTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public LexerTokenException(Throwable cause) {
        super(cause);
    }

    public LexerTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
