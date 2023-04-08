package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Runtime.TypeSystem.Expressions.BinaryExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.ErrorExpression;
import dev.fangscl.Runtime.TypeSystem.Expressions.Expression;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.BlockStatement;
import dev.fangscl.Runtime.TypeSystem.Statements.EmptyStatement;
import dev.fangscl.Runtime.TypeSystem.Statements.ExpressionStatement;
import dev.fangscl.Runtime.TypeSystem.Statements.Statement;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;

/*
 * Responsability: It does lexical analisys and source code validation.
 * Take the tokens from the lexer and create an AST.
 * Evaluation does not happen at this step only in the interpreter
 *  Main entry point:
 *  Program
 *      : StatementList
 *      ;
 *  NumericLiteral
 *      : NUMBER
 *      ;
 *  StringLiteral
 *      : STRING
 *      ;
 *  StatementList
 *      : Statement
 *      | StatementList
 *      ;
 *
 * */
@Data
@Log4j2
public class Parser {
    private List<Token> tokens;
    private ListIterator<Token> iterator;

    public Parser(List<Token> tokens) {
        setTokens(tokens);
    }

    public Parser() {
    }

    private void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.iterator = tokens.listIterator();
    }

    public Program produceAST(List<Token> tokens) {
        setTokens(tokens);
        return produceAST();
    }

    public Program produceAST() {
        var program = new Program();
        while (iterator.hasNext()) {
            Token current = eat();
            if (current.getType() == TokenType.EOF) {
                break;
            }
            Statement statement = this.parseStatement(current);
            if (statement == null) {
                continue;
            }
            program.add(statement);
        }

        return program;
    }

    @Nullable
    private Statement parseStatement(Token token) {
        return switch (token.getType()) {
            case NewLine -> new EmptyStatement();
            case OpenBraces -> {
                if (lookAhead().getType() == TokenType.CloseBraces) { // ? { } => eat } & return the block
                    eat(TokenType.CloseBraces, "Error");
                    yield new BlockStatement();
                }
                token = eat();
                var block = new BlockStatement(parseStatement(token)); // parseStatement because a block contains more statements
                yield block;
            }
            case CloseBraces -> null;
            default -> {
                var res = new ExpressionStatement(parseExpression(token));
                if (iterator.hasNext() && lookAhead().getType() == TokenType.NewLine) {
                    eat(TokenType.NewLine);
                }
                yield res;
            }
        };
    }

    private Expression parseExpression(Token token) {
        return parseAdditive(token);
    }

    private Expression parseAdditive(Token token) {
        var left = parseMultiplicative(token);

        // (10+5)-5
        while (iterator.hasNext() && lookAhead().in("+", "-")) {
            var operator = eat();
            var next = eat(); // get right hand side of an expression
            Expression right = this.parseMultiplicative(next);
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    private Expression parseMultiplicative(Token token) {
        var left = parseLiteral(token);

        // (10*5)-5
        while (iterator.hasNext() && lookAhead().in("*", "/", "%")) {
            var operator = eat();
            var next = eat(); // get right hand side of an expression
            Expression right = this.parseLiteral(next);
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    private Expression parseLiteral(Token token) {
        return switch (token.getType()) {
            case Identifier -> new Identifier(token.getValue());
            case Number -> new NumericLiteral(token.getValue());
            case String -> new StringLiteral(token.getValue());
            case OpenParenthesis -> {
                var res = parseExpression(eat());
                eat(TokenType.CloseParenthesis, "Unexpected token found inside paranthesized expression. Expected closed parenthesis.");
                yield res;
            }
            default -> new ErrorExpression(token.getValue());
        };
    }

    @Nullable
    private Token lookAhead() {
        return tokens.get(iterator.nextIndex());
    }

    private Token eat(TokenType type, String error) {
        var current = eat();
        if (current.getType() != type) {
            log.debug("Parser error\n {} {} \nExpected: {} ", error, current, type);
            throw new RuntimeException("Parser error." + error);
        }
        return current;
    }

    private Token eat(TokenType type) {
        return eat(type, "Unexpected token found");
    }

    private Token eat() {
        return iterator.next();
    }

}
