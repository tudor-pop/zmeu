package dev.fangscl.ast;

import dev.fangscl.ast.Statements.ProgramStatement;
import dev.fangscl.ast.Statements.Statement;
import dev.fangscl.ast.Statements.Expressions.*;
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
            var current = iterator.next();
            if (current.getType() == TokenType.EOF) {
                break;
            }
            program.addStatement(this.parseStatement(current));
        }

        return program;
    }

    private Statement parseStatement(Token token) {
        return parseExpression(token);
    }

    private Expression parseExpression(Token token) {
        return parseAdditive(token);
    }

    private Expression parseAdditive(Token token) {
        var left = parseMultiplicative(token);

        // (10+5)-5
        while (iterator.hasNext() && TokenType.in(tokens.get(iterator.nextIndex()).getValue(), "+", "-")) {
            var operator = iterator.next();
            var next = iterator.next(); // get right hand side of an expression
            Expression right = this.parseMultiplicative(next);
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    private Expression parseMultiplicative(Token token) {
        var left = parseLiteral(token);

        // (10*5)-5
        while (iterator.hasNext() && TokenType.in(tokens.get(iterator.nextIndex()).getValue(), "*", "/", "%")) {
            var operator = iterator.next();
            var next = iterator.next(); // get right hand side of an expression
            Expression right = this.parseLiteral(next);
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    private Expression parseLiteral(Token token) {
        return switch (token.getType()) {
            case Identifier -> new IdentifierExpression(token.getValue());
            case Integer -> new IntegerExpression(token.getValue());
            case Decimal -> new DecimalExpression(token.getValue());
            case OpenParanthesis -> {
                var res = parseExpression(iterator.next());
                expect(TokenType.CloseParanthesis, "Unexpected token found inside paranthesized expression. Expected closed paranthesis.");
                yield res;
            }
            default -> new ErrorExpression(token.getValue());
        };
    }

    private Token expect(TokenType type, String error) {
        var prev = iterator.next();
        if (prev.getType() != type) {
            log.error("Parser error\n {} {} \nExpected: {} ", error, prev , type);
            System.exit(1);
        }
        return prev;
    }

}
