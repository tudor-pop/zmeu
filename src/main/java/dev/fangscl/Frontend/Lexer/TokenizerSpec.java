package dev.fangscl.Frontend.Lexer;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class TokenizerSpec {
    static List<Entry> spec = Arrays.asList(
            new Entry("^\s+", TokenType.WhiteSpace),
            new Entry("^\\n", TokenType.WhiteSpace),
            new Entry("^==", TokenType.Equal_Equal),
            new Entry("^!=", TokenType.Bang_Equal),
            new Entry("^<=", TokenType.Less_Equal),
            new Entry("^>=", TokenType.Greater_Equal),
            new Entry("^<", TokenType.Less),
            new Entry("^>", TokenType.Greater),
            new Entry("^/(?!/)", TokenType.Division), // match / only if not followed by another / or *
            new Entry("^//.*", TokenType.Comment),
            new Entry("(\"|')[^(\"|')]*(\"|')", TokenType.String),
            new Entry("^([0-9]*[.])?[0-9]+", TokenType.Number)
    );

    @Data
    static class Entry {
        private final Pattern pattern;
        private final TokenType type;

        Entry(String pattern, TokenType type) {
            this.pattern = matcher(pattern);
            this.type = type;
        }

        private static Pattern matcher(String sequence) {
            return Pattern.compile(sequence.trim());
        }
    }
}


