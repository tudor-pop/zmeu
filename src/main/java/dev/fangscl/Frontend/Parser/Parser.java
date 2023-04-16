package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.Literal;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Runtime.TypeSystem.Expressions.*;
import dev.fangscl.Runtime.TypeSystem.Program;
import dev.fangscl.Runtime.TypeSystem.Statements.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    private Program program = new Program();

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
        return Program();
    }

    private Program Program() {
        var statements = StatementList(TokenType.EOF);
        program.setBody(statements);
        return program;
    }

    /**
     * StatementList
     * : Statement
     * | StatementList Statement
     * ;
     */
    private List<Statement> StatementList(TokenType endTokenType) {
        var statementList = new ArrayList<Statement>();
        for (; iterator.hasNext(); current = iterator.next()) {
            if (isLookahead(endTokenType)) {
                break;
            }
            Statement statement = Statement();
            if (statement == null || statement.is(NodeType.EmptyStatement)) {
                continue;
            }
            statementList.add(statement);
        }

        return statementList;
    }

    /**
     * Statement
     * : ExpressionStatement
     * | BlockStatement
     * | EmptyStatement
     * | VariableStatement
     * | IfStatement
     * ;
     */
    @Nullable
    private Statement Statement() {
        return switch (lookAhead().getType()) {
            case NewLine -> new EmptyStatement();
            case OpenBraces -> BlockStatement();
            case If -> IfStatement();
            case Var -> VariableStatement();
            case EOF -> null;
            default -> {
                var res = ExpressionStatement();
                yield res;
            }
        };
    }

    /**
     * ExpressionStatement
     * : Expression '\n'
     * ;
     */
    private Statement ExpressionStatement() {
        return new ExpressionStatement(Expression());
    }

    /**
     * BlockStatement
     * : '{' StatementList? '}'
     * ;
     */
    private Statement BlockStatement() {
        eat(TokenType.OpenBraces);
        var res = isLookahead(TokenType.CloseBraces) ?
                BlockStatement.of(EmptyStatement.of()) :
                BlockStatement.of(StatementList(TokenType.CloseBraces));
        if (isLookahead(TokenType.CloseBraces)) { // ? { } => eat } & return the block
            eat(TokenType.CloseBraces, "Error");
        }
        return res;
    }

    /**
     * VariableStatement
     * : 'var' VariableDeclarationList '\n'?
     * ;
     */
    private Statement VariableStatement() {
        eat(TokenType.Var); // # no need to eat as it is already current
        var declarations = VariableDeclarationList();
        if (isLookahead(TokenType.lineTerminator())) {
            eat(TokenType.lineTerminator());
        }
        return VariableStatement.of(declarations);
    }

    /**
     * VariableDeclarationList
     * : VariableDeclaration
     * | VariableDeclarationList ',' VariableDeclaration
     * ;
     */
    private List<VariableDeclaration> VariableDeclarationList() {
        var declarations = new ArrayList<VariableDeclaration>();
        do {
            declarations.add(VariableDeclaration());
        } while (isLookahead(TokenType.Comma) && eat(TokenType.Comma) != null);
        return declarations;
    }

    /**
     * VariableDeclaration
     * : Identifier OptVariableInitialization
     * ;
     */
    private VariableDeclaration VariableDeclaration() {
        var id = Identifier();
        var init = lookAhead().is(TokenType.lineTerminator(), TokenType.Comma, TokenType.EOF) ? null : VariableInitializer();
        return VariableDeclaration.of(id, init);
    }

    /**
     * VariableInitializer
     * : SIMPLE_ASSIGN AssignmentExpression
     */
    private Expression VariableInitializer() {
        if (lookAhead().is(TokenType.Equal, TokenType.Equal_Complex)) {
            eat(TokenType.Equal);
        }
        return AssignmentExpression();
    }

    /**
     * Expression
     * : AssignmentExpression
     * ;
     */
    private Expression Expression() {
        return AssignmentExpression();
    }

    /**
     * IfStatement
     * : 'if' '(' Expression ')' '{'? Statement '}'?
     * : 'if' '(' Expression ')' '{'? Statement '}'? 'else' '{'? Statement '}'?
     * ;
     */
    private Statement IfStatement() {
        eat(TokenType.If);
        eat(TokenType.OpenParenthesis);
        var test = Expression();
        eat(TokenType.CloseParenthesis);
        if (isLookahead(TokenType.NewLine)) {
            /* if(x)
             *   x=2
             */
            eat(TokenType.NewLine);
        }

        Statement consequent = Statement();
        Statement alternate = null;
        if (isLookahead(TokenType.Else)) {
            eat(TokenType.Else);
            alternate = Statement();
        }
        return IfStatement.of(test, consequent, alternate);
    }

    private void closeBlock() {
        if (isLookahead(TokenType.NewLine)) {
            eat(TokenType.NewLine);
        }
        eat(TokenType.CloseBraces);
    }

    private void openBlock() {
        if (isLookahead(TokenType.NewLine)) {
            eat(TokenType.NewLine);
        }
        eat(TokenType.OpenBraces);
    }

    private boolean isLookahead(TokenType type) {
        Token token = lookAhead();
        return token != null && token.is(type);
    }

    /**
     * AssignmentExpression
     * : Identifier AssignmentOperator AssignmentExpression
     */
    private Expression AssignmentExpression() {
        Expression left = AdditiveExpression();
        if (!lookAhead().isAssignment()) {
            return left;
        }
        var operator = AssignmentOperator().getValue();
        return AssignmentExpression.of(isValidAssignment(left, operator), AssignmentExpression(), operator);
    }

    /**
     * AssignmentOperator
     * : SIMPLE_ASSIGN
     * | COMPLEX_ASSIGN
     */
    private Token AssignmentOperator() {
        Token token = lookAhead();
        if (token.isAssignment()) {
            current = eat(token.getType());
            return current;
        }
        throw new RuntimeException("Unrecognized token");
    }

    private Expression isValidAssignment(Expression target, Object operator) {
        if (target.is(NodeType.Identifier)) {
            return target;
        }
        if (target instanceof Literal n)
            throw new SyntaxError("Invalid left-hand side in assignment expression: %s %s %s".formatted(n.getVal(), operator, current.getValue()));
        else
            throw new SyntaxError("Invalid left-hand side in assignment expression: %s %s %s".formatted(target, operator, current.getValue()));
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
        while (match("+", "-")) {
            var operator = eat();
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
        var left = PrimaryExpression();

        // (10*5)-5
        while (match("*", "/", "%")) {
            var operator = eat();
            Expression right = PrimaryExpression();
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    private boolean match(String... strings) {
        return iterator.hasNext() && lookAhead().is(strings);
    }

    private Expression PrimaryExpression() {
        return switch (lookAhead().getType()) {
            case OpenParenthesis -> {
                eat();
                yield ParanthesizedExpression();
            }
            case Equal -> {
                eat();
                yield AssignmentExpression();
            }
            case Number, String -> {
                eat();
                yield Literal();
            }
            case Identifier -> Identifier();
            case EOF -> null;
            default -> LeftHandSideExpression();
        };
    }

    /**
     * LeftHandSideExpression
     * : Identifier
     */
    private Expression LeftHandSideExpression() {
        return Identifier();
    }

    /**
     * Identifier
     * : IDENTIFIER
     * ;
     */
    private Expression Identifier() {
        var id = eat(TokenType.Identifier);
        return new Identifier(id.getValue());
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

    private Expression Literal() {
        return switch (current.getType()) {
            case Identifier -> new Identifier(current.getValue());
            case Number -> new NumericLiteral(current.getValue());
            case String -> new StringLiteral(current.getValue());
            default -> new ErrorExpression(current.getValue());
        };
    }

    private Token lookAhead() {
        return tokens.get(iterator.nextIndex());
    }

    private Token eat(TokenType type, String error) {
        Token lookAhead = lookAhead();
        if (lookAhead == null || lookAhead.is(TokenType.EOF)) {
            log.debug("EndOfFile reached ");
            throw new RuntimeException("Parser error." + error);
        }
        if (!lookAhead.is(type)) {
            log.debug("Parser error\n {} {} \nExpected: {} ", error, current, type);
            throw new RuntimeException("Parser error." + error);
        }
        return eat();
    }

    private Token eat(TokenType type) {
        return eat(type, "Unexpected token found");
    }

    private Token eat() {
        current = iterator.next();
        return current;
    }

}
