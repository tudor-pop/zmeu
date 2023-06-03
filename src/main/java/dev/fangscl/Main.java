package dev.fangscl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fangscl.Frontend.Lexer.Tokenizer;
import dev.fangscl.Frontend.Parser.Parser;
import dev.fangscl.Frontend.Parser.Program;
import dev.fangscl.Runtime.Interpreter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Scanner;

@Log4j2
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);

        System.out.println("REPL FCL v0.1.0");

        var mapper = new ObjectMapper();

        var interpreter = new Interpreter();
        var scanner = new Scanner(System.in);
        while (true) {
            var tokenizer = new Tokenizer();
            var parser = new Parser();
            System.out.print("> ");
            var line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("exit()") ||
                line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("quit()")) {
                System.exit(0);
            }
            Program program = parser.produceAST(tokenizer.tokenize(line));
            System.out.println(mapper.writeValueAsString(program));
            var evalRes = interpreter.eval(program);
            log.debug("{}", evalRes);
            log.debug("\n\n");
        }

    }
}