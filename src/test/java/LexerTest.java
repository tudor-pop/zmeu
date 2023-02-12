import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import dev.fangscl.lang.lexer.Lexer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.StringJoiner;

@Log4j2
public class LexerTest {

    @Test
    void checkLoop() throws IOException {
        var lexer = new Lexer();
        var lines = Files.readAllLines(Paths.get("test-1.fcl"));
        for (var line : lines) {
            var tokens = lexer.tokenize(line);
            log.error("lines: {}", tokens);
        }
    }

}
