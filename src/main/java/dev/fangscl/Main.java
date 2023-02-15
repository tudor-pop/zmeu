package dev.fangscl;

import com.google.gson.GsonBuilder;
import dev.fangscl.Parsing.Parser;
import dev.fangscl.Parsing.Lexer.Lexer;
import dev.fangscl.Runtime.Interpreter;
import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Log4j2
public class Main {
    public static void main(String[] args) {
        System.out.println("REPL FCL v0.1.0");

        var gson = new GsonBuilder()
//                .setPrettyPrinting()
                .create();
        Interpreter interpreter = new Interpreter();
        var scanner = new Scanner(System.in);
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
            var evalRes = interpreter.eval(program);
            log.error("{}", evalRes);
            log.error("\n\n");
        }

    }
}