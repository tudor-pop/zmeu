package dev.fangscl.Frontend.Lexer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TokenizerSpec {
    private static Map<TokenType, Pattern> spec = Map.ofEntries(
            Map.entry(TokenType.String, matcher("""
                    ("|')[^("|')]*("|')
                    """)),
            Map.entry(TokenType.Integer, matcher("""
                    ^([0-9]*[.])?[0-9]+
                     """)),
            Map.entry(TokenType.Decimal, matcher("""
                    ^([0-9]*[.])?[0-9]+
                     """))
    );

    private static Pattern matcher(String sequence) {
        return Pattern.compile(sequence.trim());
    }

    Pattern get(TokenType type) {
        return spec.get(type);
    }

    /**
     * This will match:
     * 123
     * 123.456
     * .456
     * https://stackoverflow.com/questions/12643009/regular-expression-for-floating-point-numbers
     *
     * @param sequence
     * @return
     */
    public static Matcher isNumber(CharSequence sequence) {
        Pattern number = Pattern.compile("^([0-9]*[.])?[0-9]+");
        var matcher = number.matcher(sequence);
        return matcher;
    }

    public static Matcher isNumber(char sequence) {
        return isNumber(String.valueOf(sequence));
    }

    public static Matcher isString(CharSequence sequence) {
        var matcher = Pattern.compile("""
                ("|')[^("|')]*("|')""").matcher(sequence);
        return matcher;
    }

}
