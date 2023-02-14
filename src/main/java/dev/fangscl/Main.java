package dev.fangscl;

import com.google.gson.GsonBuilder;
import dev.fangscl.Parsing.Parser;
import dev.fangscl.Parsing.Lexer.Lexer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("REPL FCL v0.1.0");

        var gson = new GsonBuilder()
//                .setPrettyPrinting()
                .create();
        var scanner = new Scanner(System.in);
        while (true) {
            var parser = new Parser(new Lexer());
            System.out.print("> ");
            var line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("exit()") ||
                    line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("quit()")) {
                System.exit(0);
            }
            var ast = parser.produceAST(line);
            System.out.println(gson.toJson(ast));
        }

    }
}