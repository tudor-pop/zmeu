package dev.fangscl.Frontend.Lexer;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class TokenizerSpec {
    static List<Entry> spec = Arrays.asList(
// ---------------------------------- Comments --------------------------------------------------
            // Skip single line comment: //
            new Entry("^//.*[\\n]?", TokenType.Comment),
            // Skip multiline comment: /*  */
            new Entry("^\\/\\*[\\s\\S]*?\\*\\/[\\n]?", TokenType.Comment),
// --------------------------------- Grouping operators -----------------------------------------
            new Entry("^\\{", TokenType.OpenBraces),
            new Entry("^\\}", TokenType.CloseBraces),
            new Entry("^\\(", TokenType.OpenParenthesis),
            new Entry("^\\)", TokenType.CloseParenthesis),
            new Entry("^\\[", TokenType.OpenBrackets),
            new Entry("^\\]", TokenType.CloseBrackets),
            new Entry("^,", TokenType.Comma),
// ---------------------------------- Literals ----------------------------------------------
            // match string: " str ", ' str '
            new Entry("^(\"|')[^(\"|')]*(\"|')", TokenType.String),
            // match numbers: 1, 111.222
            new Entry("^([0-9]*[.])?[0-9]+", TokenType.Number),

// --------------------------------- Whitespace --------------------------------------------------------
            new Entry("^\\n", TokenType.lineTerminator()),
            new Entry("^\\s+", TokenType.WhiteSpace),
//            new Entry("^;", TokenType.LineTerminator),

// --------------------------------- Assignment --------------------------------------------------------
            new Entry("^[!=]=", TokenType.Equality_Operator),
            // =, +=, -=, /=, *=
            new Entry("^=", TokenType.Equal),
            new Entry("^[\\+\\-\\*\\/]=", TokenType.Equal_Complex),

// --------------------------------- Operators --------------------------------------------------------
            new Entry("^\\+", TokenType.Plus),
            new Entry("^\\*", TokenType.Multiply),
            //todo add exponentiation ^
            new Entry("^-", TokenType.Minus),
            new Entry("^<=", TokenType.Less_Equal),
            new Entry("^>=", TokenType.Greater_Equal),
            new Entry("^<", TokenType.Less),
            new Entry("^>", TokenType.Greater),
            // match / only if not followed by another /
            new Entry("^/(?!/)", TokenType.Division),

// --------------------------------- Keywords --------------------------------------------------------
            new Entry("^\\bvar\\b", TokenType.Var),
            new Entry("^\\bif\\b", TokenType.If),
            new Entry("^\\belse\\b", TokenType.Else),
            new Entry("^\\btrue\\b", TokenType.True),
            new Entry("^\\bfalse\\b", TokenType.False),
            new Entry("^\\bnull\\b", TokenType.Null),
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


