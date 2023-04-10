package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Runtime.TypeSystem.Expressions.AssignmentExpression;
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
    private Token current;

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
        for (current = iterator.next(); iterator.hasNext() && current.getType() != TokenType.EOF; current = iterator.next()) {
            Statement statement = Statement();
            if (statement == null) {
                continue;
            }
            program.add(statement);
        }

        return program;
    }

    @Nullable
    private Statement Statement() {
        return switch (current.getType()) {
            case NewLine -> new EmptyStatement();
            case OpenBraces -> {
                if (lookAhead().getType() == TokenType.CloseBraces) { // ? { } => eat } & return the block
                    eat(TokenType.CloseBraces, "Error");
                    yield new BlockStatement();
                }
                current = eat();
                var block = new BlockStatement(Statement()); // parseStatement because a block contains more statements
                yield block;
            }
            case CloseBraces, EOF -> null;
            default -> {
                var res = new ExpressionStatement(Expression());
                if (iterator.hasNext() && lookAhead().is(TokenType.NewLine)) {
                    eat(TokenType.NewLine);
                }
                yield res;
            }
        };
    }

    private Expression Expression() {
        return AssignmentExpression();
    }

    /**
     * AssignmentExpression
     * : Identifier AssignmentOperator AssignmentExpression
     */
    private Expression AssignmentExpression() {
        Expression left = AdditiveExpression();
        if (!lookAhead().is(TokenType.Equal)) {
            return left;
        }
        var operator = AssignmentOperator();
        return AssignmentExpression.of(checkValidAssignment(left), AssignmentExpression(), operator);
    }

    /**
     * AssignmentOperator
     * : SIMPLE_ASSIGN
     * | COMPLEX_ASSIGN
     */
    private Object AssignmentOperator() {
        if (lookAhead().is(TokenType.Equal)) {
            return eat(TokenType.Equal);
        }
        return eat(TokenType.Equal_Complex);
    }

    /**
     * LeftHandSideExpression
     * : Identifier
     */
    private Object leftHandSideExpression() {
        return Identifier();
    }

    /**
     * Identifier
     * : IDENTIFIER
     * ;
     */
    private Expression Identifier() {
        return Literal(eat());
    }

    private Expression checkValidAssignment(Expression target) {
        if (target.is(NodeType.Identifier)) {
            return target;
        }
        throw new SyntaxError("Invalid left-hand side in assignment expression");
    }


    /**
     * AdditiveExpression
     * : MultiplicativeExpression
     * | AdditiveExpression ADDITIVE_OPERATOR MultiplicativeExpression -> MultiplicativeExpression ADDITIVE_OPERATOR MultiplicativeExpression
     * ;
     */
    private Expression AdditiveExpression() {
        var left = MultiplicativeExpression();

        // (10+5)-5
        while (iterator.hasNext() && lookAhead().is("+", "-")) {
            var operator = eat();
            current = eat();
            Expression right = this.MultiplicativeExpression();
            left = BinaryExpression.of(left, right, operator.getValue());
        }

        return left;
    }

    /**
     * MultiplicativeExpression
     * : PrimaryExpression
     * | MultiplicativeExpression MULTIPLICATIVE_OPERATOR PrimaryExpression -> PrimaryExpression MULTIPLICATIVE_OPERATOR PrimaryExpression
     * ;
     */
    private Expression MultiplicativeExpression() {
//        current = eat();
        var left = PrimaryExpression();

        // (10*5)-5
        while (iterator.hasNext() && lookAhead().is("*", "/", "%")) {
            var operator = eat();
            current = eat();
            Expression right = PrimaryExpression();
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    private Expression PrimaryExpression() {
//        var lookahead = lookAhead();
        return switch (current.getType()) {
            case OpenParenthesis -> {
                current = eat();
                yield ParanthesizedExpression();
            }
            case EOF -> null;
            default -> Literal(current);
        };
    }

    /**
     * ParanthesizedExpression
     * : '(' Expression ')'
     * ;
     */
    private Expression ParanthesizedExpression() {
//        eat(TokenType.OpenParenthesis);
        var res = Expression();
        eat(TokenType.CloseParenthesis, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
        return res;
    }

    private Expression Literal(Token token) {
        return switch (token.getType()) {
            case Identifier -> new Identifier(token.getValue());
            case Number -> new NumericLiteral(token.getValue());
            case String -> new StringLiteral(token.getValue());
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
