package dev.fangscl.Frontend.Lexer;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class TokenizerSpec {
    static List<Entry> spec = Arrays.asList(
//            skip comment
            new Entry("^//.*", TokenType.Comment),
            new Entry("^\\/\\*[\\s\\S]*?\\*\\/", TokenType.Comment),

            new Entry("^\\{", TokenType.OpenBraces),
            new Entry("^\\}", TokenType.CloseBraces),
            new Entry("^\\(", TokenType.OpenParenthesis),
            new Entry("^\\)", TokenType.CloseParenthesis),
            new Entry("^\\[", TokenType.OpenBrackets),
            new Entry("^\\]", TokenType.CloseBrackets),

            new Entry("^(\"|')[^(\"|')]*(\"|')", TokenType.String),
            new Entry("^([0-9]*[.])?[0-9]+", TokenType.Number),
            new Entry("^\\n", TokenType.NewLine),
            new Entry("^\\s+", TokenType.WhiteSpace),
//            new Entry("^;", TokenType.LineTerminator),
            new Entry("^==", TokenType.Equal_Equal),
            new Entry("^=", TokenType.Equal),
            new Entry("^\\+", TokenType.Plus),
            new Entry("^\\*", TokenType.Multiply),
            new Entry("^-", TokenType.Minus),
            new Entry("^!=", TokenType.Bang_Equal),
            new Entry("^<=", TokenType.Less_Equal),
            new Entry("^>=", TokenType.Greater_Equal),
            new Entry("^<", TokenType.Less),
            new Entry("^>", TokenType.Greater),
            // match / only if not followed by another /
            new Entry("^/(?!/)", TokenType.Division),

            new Entry("^(var)", TokenType.Var),
            new Entry("^\\w+", TokenType.Identifier)
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


