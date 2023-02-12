package dev.fangscl.ast;

import dev.fangscl.ast.statements.ProgramStatement;
import dev.fangscl.ast.statements.Statement;
import dev.fangscl.ast.statements.expressions.*;
import dev.fangscl.lexer.Lexer;
import dev.fangscl.lexer.Token;
import dev.fangscl.lexer.TokenType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.ListIterator;

/*
 * Responsability: take the tokens from the lexer and create an AST
 *
 * */
@Data
@Log4j2
public class Parser {
    private List<Token> tokens;
    private ListIterator<Token> iterator;
    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public ProgramStatement produceAST(String src) {
        var program = new ProgramStatement();

        tokens = lexer.tokenize(src);
        iterator = tokens.listIterator();

        while (iterator.hasNext()) {
            var token = iterator.next();
            if (token.getType() == TokenType.EOF) {
                break;
            }
            program.addStatement(this.parseStatement(token));
        }

        return program;
    }

    private Statement parseStatement(Token token) {
        return parseAdditive(token);
    }

    private Expression parseLiteral(Token token) {
        return switch (token.getType()) {
            case Identifier -> new IdentifierExpression(token.getValue());
            case Integer -> new IntegerExpression(token.getValue());
            case Decimal -> new DecimalExpression(token.getValue());
            default -> new Expression();
        };
    }

    private Expression parseAdditive(Token token) {
        var left = parseLiteral(token);

        // (10+5)-5
        for (var operator = iterator.next(); iterator.hasNext() && TokenType.in(operator.getValue(), "+", "-"); ) {
            var next = iterator.next(); // get right hand side of an expression
            Expression right = this.parseLiteral(next);
            left = new BinaryExpression(left, right, operator.getValue());
            if (iterator.hasNext()) {
                operator = iterator.next();
            }
        }

        return left;
    }

}
