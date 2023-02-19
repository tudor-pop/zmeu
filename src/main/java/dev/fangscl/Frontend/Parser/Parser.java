package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Lexer;
import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.ErrorExpression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Runtime.TypeSystem.Program;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.ListIterator;

/*
 * Responsability: It does lexical analisys and source code validation. Take the tokens from the lexer and create an AST.
 *  Main entry point:
 *  Program
 *      : NumericLiteral
 *      ;
 *  NumericLiteral
 *      : NUMBER
 *      ;
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

    public Program produceAST(String src){
        tokens = lexer.tokenize(src);
        iterator = tokens.listIterator();

        return produceAST();
    }

    private Program produceAST() {
        var program = new Program();
        while (iterator.hasNext()) {
            Token current = iterator.next();
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
            case Identifier -> new Identifier(token.getValue());
            case Decimal, Integer -> new NumericLiteral(token.getValue());
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
            log.debug("Parser error\n {} {} \nExpected: {} ", error, prev, type);
            System.exit(1);
        }
        return prev;
    }

}
