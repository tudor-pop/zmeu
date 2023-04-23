package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Statements.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
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
            if (IsLookAhead(endTokenType)) { // need to check for EOF before doing any work
                break;
            }
            Statement statement = Statement();
            if (statement == null || statement.is(NodeType.EmptyStatement)) {
                continue;
            }
            statementList.add(statement);
            if (IsLookAhead(endTokenType)) {
                // after some work is done, before calling iterator.next(),
                // we must check for EOF again or else we risk going outside the iterators bounds
                break;
            }
        }

        return statementList;
    }

    /**
     * Statement
     * : ExpressionStatement
     * | EmptyStatement
     * | BlockExpression
     * | VariableStatement
     * | IfStatement
     * | IterationStatement
     * | ForStatement
     * | FunctionDeclarationStatement
     * | ReturnStatement
     * ;
     */
    @Nullable
    private Statement Statement() {
        return switch (lookAhead().getType()) {
            case NewLine -> new EmptyStatement();
            case OpenBraces -> BlockExpression();
            case If -> IfStatement();
            case Fun -> FunctionDeclarationStatement();
            case Return -> ReturnStatement();
            case While, For -> IterationStatement();
            case Var -> VariableStatement();
            case EOF -> null;
            default -> {
                var res = ExpressionStatement();
                yield res;
            }
        };
    }

    /**
     * IterationStatement
     * : WhileStatement
     * | ForStatement
     * ;
     */
    private Statement IterationStatement() {
        return switch (lookAhead().getType()) {
            case While -> WhileStatement();
            case For -> ForStatement();
            default -> throw new SyntaxError();
        };
    }

    /**
     * WhileStatement
     * : while ( Expression ) {? StatementList }?
     * ;
     */
    private Statement WhileStatement() {
        eat(TokenType.While);
        eat(TokenType.OpenParenthesis);
        var test = Expression();
        eat(TokenType.CloseParenthesis);
        if (IsLookAhead(TokenType.NewLine)) {
            /* while(x)
             *   x=2
             */
            eat(TokenType.NewLine);
        }

        var statement = Statement();
        return WhileStatement.of(test, statement);
    }

    /**
     * ForStatement
     * : for ( OptStatementInit ; OptExpression ; OptExpression ) Statement
     * ;
     */
    private Statement ForStatement() {
        eat(TokenType.For);
        eat(TokenType.OpenParenthesis);

        Statement init = lookAhead().is(TokenType.SemiColon) ? null : ForStatementInit();
        eat(TokenType.SemiColon);

        var test = lookAhead().is(TokenType.SemiColon) ? null : Expression();
        eat(TokenType.SemiColon);

        var update = lookAhead().is(TokenType.CloseParenthesis) ? null : Expression();
        eat(TokenType.CloseParenthesis);
        if (IsLookAhead(TokenType.NewLine)) {
            /* while(x)
             *   x=2
             */
            eat(TokenType.NewLine);
        }

        var body = Statement();
        return ForStatement.builder()
                .test(test)
                .body(body)
                .init(init)
                .update(update)
                .build();
    }

    /**
     * ForStatementInit
     * : VariableStatementInit
     * | Expression
     */
    private Statement ForStatementInit() {
        if (lookAhead().is(TokenType.Var)) {
            return VariableStatementInit();
        }
        return Expression();
    }

    /**
     * VariableStatementInit
     * : var VariableStatements
     */
    private Statement VariableStatementInit() {
        eat(TokenType.Var);
        var declarations = VariableDeclarationList();
        return VariableStatement.of(declarations);
    }

    /**
     * VariableStatement
     * : var VariableDeclarations \n?
     * ;
     */
    private Statement VariableStatement() {
//        eat(TokenType.Var); // # no need to eat as it is already current
        var statement = VariableStatementInit();
        if (IsLookAhead(TokenType.lineTerminator())) {
//            eat(TokenType.lineTerminator());
        }
        return statement;
    }

    /**
     * ExpressionStatement
     * : Expression \n
     * ;
     */
    private Statement ExpressionStatement() {
        return ExpressionStatement.of(Expression());
    }

    /**
     * BlockExpression
     * : { Statements? }
     * ;
     * Statements
     * : Statement* Expression
     */
    private Expression BlockExpression() {
        eat(TokenType.OpenBraces);
        var res = IsLookAhead(TokenType.CloseBraces)
                ? BlockExpression.of(Collections.emptyList())
                : BlockExpression.of(StatementList(TokenType.CloseBraces));
        if (IsLookAhead(TokenType.CloseBraces)) { // ? { } => eat } & return the block
            eat(TokenType.CloseBraces, "Error");
        }
        return res;
    }

    /**
     * VariableDeclarationList
     * : VariableDeclaration
     * | VariableDeclarationList , VariableDeclaration
     * ;
     */
    private List<VariableDeclaration> VariableDeclarationList() {
        var declarations = new ArrayList<VariableDeclaration>();
        do {
            declarations.add(VariableDeclaration());
        } while (IsLookAhead(TokenType.Comma) && eat(TokenType.Comma) != null);
        return declarations;
    }

    /**
     * VariableDeclaration
     * : Identifier VariableInitialization?
     * ;
     */
    private VariableDeclaration VariableDeclaration() {
        var id = Identifier();
        var init = lookAhead().is(TokenType.lineTerminator(), TokenType.Comma, TokenType.EOF) ? null : VariableInitializer();
        return VariableDeclaration.of(id, init);
    }

    /**
     * VariableInitializer
     * : SIMPLE_ASSIGN Expression
     */
    private Expression VariableInitializer() {
        if (lookAhead().is(TokenType.Equal, TokenType.Equal_Complex)) {
            eat(TokenType.Equal);
        }
        return Expression();
    }

    /**
     * Expression
     * : AssignmentExpression
     * | BlockExpression
     * ;
     */
    private Expression Expression() {
        return switch (lookAhead().getType()) {
            case OpenBraces -> BlockExpression();
            default -> AssignmentExpression();
        };
    }

    /**
     * IfStatement
     * : if ( Expression ) BlockStatement?
     * : if ( Expression ) BlockStatement? else BlockStatement?
     * ;
     */
    private Statement IfStatement() {
        eat(TokenType.If);
        eat(TokenType.OpenParenthesis);
        var test = Expression();
        eat(TokenType.CloseParenthesis);
        if (IsLookAhead(TokenType.NewLine)) {
            /* if(x)
             *   x=2
             */
            eat(TokenType.NewLine);
        }

        Statement consequent = Statement();
        Statement alternate = null;
        if (IsLookAhead(TokenType.Else)) {
            eat(TokenType.Else);
            alternate = Statement();
        }
        return IfStatement.of(test, consequent, alternate);
    }

    /**
     * FunctionDeclarationStatement
     * : fun Identifier ( OptParameterList ) BlockStatement?
     * ;
     */
    private Statement FunctionDeclarationStatement() {
        eat(TokenType.Fun);
        var test = Identifier();
        eat(TokenType.OpenParenthesis);
        var params = OptParameterList();
        eat(TokenType.CloseParenthesis);

        Statement body = BlockExpression();
        return FunctionDeclarationStatement.of(test, params, body);
    }

    private List<Identifier> OptParameterList() {
        return lookAhead().is(TokenType.CloseParenthesis) ? Collections.emptyList() : ParameterList();
    }

    /**
     * ParameterList
     * : Identifier
     * | ParameterList, Identifier
     * ;
     */
    private List<Identifier> ParameterList() {
        var params = new ArrayList<Identifier>();
        do {
            params.add(Identifier());
        } while (IsLookAhead(TokenType.Comma) && eat(TokenType.Comma) != null);

        return params;
    }

    /**
     * ReturnStatement
     * : return OptExpression \n
     * ;
     */
    private Statement ReturnStatement() {
        eat(TokenType.Return);
        var arg = OptExpression();
        eat(TokenType.lineTerminator());
        return ReturnStatement.of(arg);
    }

    private Expression OptExpression() {
        return IsLookAhead(TokenType.lineTerminator()) ? null : Expression();
    }

    private boolean IsLookAhead(TokenType... type) {
        if (!iterator.hasNext()) {
            return false;
        }
        Token token = lookAhead();
        return token != null && token.is(type);
    }

    /**
     * AssignmentExpression
     * : LogicalExpression
     * | LeftHandSideExpression AssignmentOperator Expression
     */
    private Expression AssignmentExpression() {
        Expression left = OrExpression();
        if (!IsLookAhead(TokenType.Equal, TokenType.Equal_Complex)) {
            return left;
        }
        var operator = AssignmentOperator().getValue();
        return AssignmentExpression.of(isValidAssignment(left, operator), Expression(), operator);
    }

    /**
     * Logical LOGICAL_OPERATOR Expressions: ||
     * x || y
     * AndExpression
     * : AndExpression LOGICAL_OPERATOR OrExpression
     * | EqualityExpression
     * ;
     */
    private Expression OrExpression() {
        var expression = AndExpression();
        if (IsLookAhead(TokenType.EOF) || !IsLookAhead(TokenType.Logical_Or)) {
            return expression;
        }
        var operator = eat();
        return LogicalExpression.of(operator.getValue(), expression, OrExpression());
    }

    /**
     * Logical LOGICAL_OPERATOR Expressions: &&, ||
     * x && y
     * x || y
     * AndExpression
     * : EqualityExpression LOGICAL_OPERATOR AndExpression
     * | EqualityExpression
     * ;
     */
    private Expression AndExpression() {
        var expression = EqualityExpression();
        if (IsLookAhead(TokenType.EOF) || !IsLookAhead(TokenType.Logical_And)) {
            return expression;
        }
        var operator = eat();
        return LogicalExpression.of(operator.getValue(), expression, AndExpression());
    }

    /**
     * EQUALITY_OPERATOR: == !=
     * x == y
     * x != y
     * EqualityExpression
     * : RelationalExpression EQUALITY_OPERATOR EqualityExpression
     * | RelationalExpression
     * ;
     */
    private Expression EqualityExpression() {
        var expression = RelationalExpression();
        if (IsLookAhead(TokenType.EOF) || !IsLookAhead(TokenType.Equality_Operator)) {
            return expression;
        }
        var operator = eat();
        return BinaryExpression.of(expression, EqualityExpression(), operator.getValue());
    }

    /**
     * RELATIONAL_OPERATOR: >,>=,<=,<
     * x > y
     * x >= y
     * x < y
     * x <= y
     * RelationalExpression
     * : AdditiveExpression
     * | AdditiveExpression RELATIONAL_OPERATOR RelationalExpression
     * ;
     */
    private Expression RelationalExpression() {
        var expression = AdditiveExpression();
        if (IsLookAhead(TokenType.EOF) || !IsLookAhead(TokenType.RelationalOperator)) {
            return expression;
        }
        var operator = eat();
        return BinaryExpression.of(expression, RelationalExpression(), operator.getValue());
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
        if (target.is(NodeType.Identifier, NodeType.MemberExpression)) {
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
    @Nullable
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
     * : UnaryExpression
     * | MultiplicativeExpression MULTIPLICATIVE_OPERATOR UnaryExpression -> PrimaryExpression MULTIPLICATIVE_OPERATOR PrimaryExpression
     * ;
     */
    private Expression MultiplicativeExpression() {
        var left = UnaryExpression();

        // (10*5)-5
        while (match("*", "/", "%")) {
            var operator = eat();
            Expression right = UnaryExpression();
            left = new BinaryExpression(left, right, operator.getValue());
        }

        return left;
    }

    /**
     * UnaryExpression
     * : LeftHandSideExpression
     * | ADDITIVE_OPERATOR UnaryExpression
     * | LOGICAL_NOT UnaryExpression
     * ;
     */
    private Expression UnaryExpression() {
        var operator = switch (lookAhead().getType()) {
            case Minus -> eat(TokenType.Minus);
            case Plus -> eat(TokenType.Plus);
            case Logical_Not -> eat(TokenType.Logical_Not);
            default -> null;
        };
        if (operator != null) {
            return UnaryExpression.of(operator.getValue(), UnaryExpression());
        }

        return LeftHandSideExpression();
    }

    private boolean match(String... strings) {
        return iterator.hasNext() && lookAhead().is(strings);
    }

    @Nullable
    private Expression PrimaryExpression() {
        if (lookAhead() == null) {
            return null;
        }
        return switch (lookAhead().getType()) {
            case OpenParenthesis -> {
                eat();
                yield ParanthesizedExpression();
            }
            case OpenBrackets -> {
//                eat();
                yield ParanthesizedExpression();
            }
            case Equal -> {
                eat();
                yield AssignmentExpression();
            }
            case Equality_Operator -> Literal();
            case Number, String, True, False, Null -> /* literals */{
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
     * : CallExpression
     * ;
     */
    private Expression LeftHandSideExpression() {
        return CallMemberExpression();
    }

    /**
     * CallMemberExpression
     * : MemberExpression
     * | CallExpression
     * ;
     */
    private Expression CallMemberExpression() {
        var member = MemberExpression();
        if (IsLookAhead(TokenType.OpenParenthesis)) {
            return CallExpression(member);
        }
        return member;
    }

    /**
     * CallExpression
     * : Callee Arguments
     * ;
     * Callee
     * : MemberExpression
     * | CallExpression
     * ;
     */
    private Expression CallExpression(Expression member) {
        var res = CallExpression.of(member, Arguments());
        if (IsLookAhead(TokenType.OpenParenthesis)) {
            res = CallExpression(res);
        }
        return res;
    }

    /**
     * Arguments
     * : ( OptArgumentsList )
     * ;
     */
    private List<Expression> Arguments() {
        eat(TokenType.OpenParenthesis);
        var list = IsLookAhead(TokenType.CloseParenthesis) ? Collections.<Expression>emptyList() : ArgumentList();
        eat(TokenType.CloseParenthesis);
        return list;
    }

    /**
     * ArgumentList
     * : AssignmentExpression
     * | ArgumentList , AssignmentExpression
     */
    private List<Expression> ArgumentList() {
        var arguments = new ArrayList<Expression>();
        do {
            arguments.add(AssignmentExpression());
        } while (!IsLookAhead(TokenType.EOF) && IsLookAhead(TokenType.Comma) && eat(TokenType.Comma) != null);

        return arguments;
    }

    /**
     * MemberExpression
     * : PrimaryExpression
     * | MemberExpression . MemberExpression
     * | MemberExpression [ Expression ]
     * ;
     */
    private Expression MemberExpression() {
        var object = PrimaryExpression();
        for (var next = lookAhead(); IsLookAhead(TokenType.Dot) || IsLookAhead(TokenType.OpenBrackets); next = lookAhead()) {
            object = switch (next.getType()) {
                case Dot -> {
                    var property = MemberProperty();
                    yield MemberExpression.of(false, object, property);
                }
                case OpenBrackets -> {
                    var property = MemberPropertyIndex();
                    yield MemberExpression.of(true, object, property);
                }
                default -> throw new IllegalStateException("Unexpected value: " + next.getType());
            };
        }
        return object;
    }

    private Expression MemberPropertyIndex() {
        eat(TokenType.OpenBrackets);
        var property = Expression();
        eat(TokenType.CloseBrackets);
        return property;
    }

    private Expression MemberProperty() {
        eat(TokenType.Dot);
        return Identifier();
    }

    /**
     * Identifier
     * : IDENTIFIER
     * ;
     */
    private Identifier Identifier() {
        var id = eat(TokenType.Identifier);
        return new Identifier(id.getValue());
    }

    /**
     * ParanthesizedExpression
     * : ( Expression )
     * ;
     */
    private Expression ParanthesizedExpression() {
//        eat(TokenType.OpenParenthesis);
        var res = Expression();
        if (lookAhead().is(TokenType.CloseParenthesis)) {
            eat(TokenType.CloseParenthesis, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
        } else if (lookAhead().is(TokenType.CloseBraces)) {
            eat(TokenType.CloseBraces, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
        }
        return res;
    }

    /**
     * Literal
     * : NumericLiteral
     * | BooleanLiteral
     * | StringLiteral
     * | NullLiteral
     * ;
     */
    private Expression Literal() {
        return switch (current.getType()) {
            case True, False -> BooleanLiteral();
            case Null -> NullLiteral.of();
            case Number -> new NumericLiteral(current.getValue());
            case String -> new StringLiteral(current.getValue());
            default -> new ErrorExpression(current.getValue());
        };
    }

    /**
     * BooleanLiteral
     * : true
     * | false
     * ;
     */
    private Expression BooleanLiteral() {
//        var literal = eat();
        return BooleanLiteral.of(current.getValue());
    }

    private Token lookAhead() {
        if (!iterator.hasNext()) {
            return null;
        }
        return tokens.get(iterator.nextIndex());
    }

    private Token eat(TokenType type, String error) {
        Token lookAhead = lookAhead();
        if (lookAhead == null || lookAhead.is(TokenType.EOF)) {
            log.debug("EndOfFile reached ");
            throw new RuntimeException("Parser error." + error);
        }
        if (!lookAhead.is(type)) {
            var err = "%s %s \n".formatted(error, current);
            log.debug(err);
            throw new SyntaxError(err);
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
