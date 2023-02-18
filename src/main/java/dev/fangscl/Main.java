package dev.fangscl;

import com.google.gson.GsonBuilder;
import dev.fangscl.Parsing.Lexer;
import dev.fangscl.Parsing.Parser;
import dev.fangscl.Runtime.Interpreter;
import dev.fangscl.Runtime.Scope.Scope;
import dev.fangscl.Runtime.Values.BooleanValue;
import dev.fangscl.Runtime.Values.IntegerValue;
import dev.fangscl.Runtime.Values.NullValue;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Scanner;

@Log4j2
public class Main {
    public static void main(String[] args) {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);

        System.out.println("REPL FCL v0.1.0");

        var gson = new GsonBuilder()
//                .setPrettyPrinting()
                .create();
        var interpreter = new Interpreter();
        var scanner = new Scanner(System.in);
        var scope = new Scope();
        scope.declareVar("x", new IntegerValue(12));
        scope.declareVar("true", new BooleanValue(true));
        scope.declareVar("false", new BooleanValue(false));
        scope.declareVar("null", new NullValue());
        while (true) {
            var parser = new Parser(new Lexer());
            System.out.print("> ");
            var line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("exit()") ||
                    line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("quit()")) {
                System.exit(0);
            }
            var program = parser.produceAST(line);
            System.out.println(gson.toJson(program));
            var evalRes = interpreter.eval(program, scope);
            log.debug("{}", evalRes);
            log.debug("\n\n");
        }

    }
}