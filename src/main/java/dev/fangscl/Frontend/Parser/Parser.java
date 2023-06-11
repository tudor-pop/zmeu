package dev.fangscl.Frontend.Parser;

import dev.fangscl.Frontend.Lexer.Token;
import dev.fangscl.Frontend.Lexer.TokenType;
import dev.fangscl.Frontend.Parser.Expressions.*;
import dev.fangscl.Frontend.Parser.Literals.*;
import dev.fangscl.Frontend.Parser.Statements.*;
import dev.fangscl.Frontend.visitors.SyntaxPrinter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/**
 * Name         Operators       Associates
 * Equality     == !=           Left
 * Comparison   < >= < <=       Left
 * Term         - +             Left
 * Factor       * /             Left
 * Unary        ! -             Right
 * Each rule here only matches expressions at its precedence level or higher.
 * For example, unary matches a unary expression like !negated or a primary expression like 1234
 * And term can match 1 + 2 but also 3 * 4 / 5. The final primary rule covers the highest-precedence
 * forms—literals and parenthesized expressions.
 * <p>
 * Expression -> Equality
 * Equality -> Comparison ( ("==" | "!=") Comparison)* ;
 * Comparison -> Term ( ( "!=" | "==" ) Term )* ;
 * Term -> Factor ( ( "-" | "+" ) Factor )* ;
 * Factor -> Unary ( ( "/" | "*" ) Unary )* ;
 * Unary -> ( "!" | "-" ) Unary
 * | Primary ;
 * Primary -> NUMBER
 * | STRING
 * | "true"
 * | "false"
 * | "null"
 * | "(" Expression ")"
 * <p>
 * ----------------------------------------------------------
 * Grammar notation         Code representation
 * -----------------------------------------------------------
 * Terminal                 Code to match and consume a token
 * Nonterminal              Call to that rule’s function
 * |                        if or switch statement
 * + or *                   while or for loop
 * ?                        if statement
 * ------------------------------------------------------------
 */
@Data
@Log4j2
public class Parser {
    private ParserIterator iterator;
    private Program program = new Program();
    private SyntaxPrinter printer = new SyntaxPrinter();

    public Parser(List<Token> tokens) {
        setTokens(tokens);
    }

    public Parser() {
    }

    private void setTokens(List<Token> tokens) {
        iterator = new ParserIterator(tokens);
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
        for (; iterator.hasNext(); eat()) {
            if (IsLookAhead(endTokenType)) { // need to check for EOF before doing any work
                break;
            }
            Statement statement = Declaration();
            if (statement == null || statement.is(NodeType.EmptyStatement)) {
                if (iterator.hasNext()) {
                    continue;
                } else {
                    break;
                }
            }
            if (iterator.getCurrent().isLineTerminator()) { // if we eat too much - going beyond lineTerminator -> go back 1 token
                iterator.prev();
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

    private Statement Declaration() {
        try {
            return switch (lookAhead().getType()) {
                case Fun -> FunctionDeclaration();
                case Schema -> SchemaDeclaration();
                case Resource -> ResourceDeclaration();
                case Var -> VariableDeclarations();
                default -> Statement();
            };
        } catch (RuntimeException error) {
            iterator.synchronize();
            return null;
        }
    }

    /**
     * {@snippet :
     * : Statement
     * | EmptyStatement
     * | VariableStatement
     * | IfStatement
     * | IterationStatement
     * | ForStatement
     * | FunctionDeclarationStatement
     * | SchemaDeclarationStatement
     * | ReturnStatement
     * | ExpressionStatement
     * ;
     *}
     */
    @Nullable
    private Statement Statement() {
        return switch (lookAhead().getType()) {
            case NewLine -> new EmptyStatement();
//            case OpenBraces -> BlockStatement();
            case If -> IfStatement();
            case Init -> InitStatement();
            case Return -> ReturnStatement();
            case While, For -> IterationStatement();
            case EOF -> null;
            default -> ExpressionStatement();
        };
    }

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

        Statement init = ForStatementInit();
        eat(TokenType.SemiColon);

        var test = ForStatementTest();
        eat(TokenType.SemiColon);

        var update = ForStatementIncrement();
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

    @Nullable
    private Expression ForStatementIncrement() {
        return IsLookAhead(TokenType.CloseParenthesis) ? null : Expression();
    }

    @Nullable
    private Expression ForStatementTest() {
        return IsLookAhead(TokenType.SemiColon) ? null : Expression();
    }

    private Statement ForStatementInit() {
        return switch (lookAhead().getType()) {
            case Var -> {
                eat(TokenType.Var);
                yield VariableStatementInit();
            }
            case SemiColon -> null;
            default -> ExpressionStatement();
        };
    }

    /**
     * VariableStatement
     * : var VariableDeclarations LineTerminator
     * ;
     */
    private Statement VariableDeclarations() {
        eat(TokenType.Var);
        var statement = VariableStatementInit();
        iterator.eatLineTerminator();
//        iterator.prev();
        return statement;
    }

    /**
     * VariableStatementInit
     * : var VariableStatements ";"
     */
    private Statement VariableStatementInit() {
        var declarations = VariableDeclarationList();
        return VariableStatement.of(declarations);
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
     * BlockStatement
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
        var init = IsLookAhead(TokenType.lineTerminator(), TokenType.Comma, TokenType.EOF) ? null : VariableInitializer();
        return VariableDeclaration.of(id, init);
    }

    /**
     * VariableInitializer
     * : SIMPLE_ASSIGN Expression
     */
    private Expression VariableInitializer() {
        if (IsLookAhead(TokenType.Equal, TokenType.Equal_Complex)) {
            eat(TokenType.Equal, TokenType.Equal_Complex);
        }
        return Expression();
    }

    private Expression Expression() {
        return switch (lookAhead().getType()) {
            case OpenBraces -> BlockExpression();
            default -> AssignmentExpression();
        };
    }

    /**
     * IfStatement
     * : if ( Expression ) Statement? (else Statement)?
     * ;
     */
    private Statement IfStatement() {
        eat(TokenType.If);
        eat(TokenType.OpenParenthesis);
        var test = Expression();
        eat(TokenType.CloseParenthesis);
        if (IsLookAhead(TokenType.NewLine)) {
            // if(x) x=2
            eat(TokenType.NewLine);
        }

        Statement ifBlock = Statement();
        Statement elseBlock = ElseStatement();
        return IfStatement.of(test, ifBlock, elseBlock);
    }

    private Statement ElseStatement() {
        if (IsLookAhead(TokenType.Else)) {
            eat(TokenType.Else);
            Statement alternate = Statement();
            iterator.eatIf(TokenType.CloseBraces);
            return alternate;
        }
        return null;
    }

    /**
     * FunctionDeclarationStatement
     * : fun Identifier ( OptParameterList ) BlockStatement?
     * ;
     */
    private Statement FunctionDeclaration() {
        eat(TokenType.Fun, "Fun token expected: " + lookAhead());
        var test = Identifier();
        eat(TokenType.OpenParenthesis, "Expected '(' but got: " + lookAhead());
        List<Expression> params = OptParameterList();
        eat(TokenType.CloseParenthesis, "Expected ')' but got: " + lookAhead());

        Statement body = ExpressionStatement.of(BlockExpression());
        return FunctionDeclaration.of(test, params, body);
    }

    /**
     * SchemaDeclaration
     * : schema Identifier BlockStatement
     * ;
     */
    private Statement SchemaDeclaration() {
        eat(TokenType.Schema);
        var test = Identifier();

        Statement body = ExpressionStatement.of(BlockExpression());
        return SchemaDeclaration.of(test, body);
    }

    /**
     * InitStatement
     * : init  ( OptParameterList ) BlockStatement?
     * ;
     */
    private Statement InitStatement() {
        eat(TokenType.Init);
        eat(TokenType.OpenParenthesis);
        List<Expression> params = OptParameterList();
        eat(TokenType.CloseParenthesis);

        Statement body = ExpressionStatement.of(BlockExpression());
        return InitStatement.of(params, body);
    }

    private List<Expression> OptParameterList() {
        return IsLookAhead(TokenType.CloseParenthesis) ? Collections.emptyList() : ParameterList();
    }

    /**
     * ParameterList
     * : Identifier
     * | ParameterList, Identifier
     * ;
     */
    private List<Expression> ParameterList() {
        var params = new ArrayList<Expression>();
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

    /**
     * LambdaExpression
     * : ( OptParameterList ) -> LambdaBody
     * | (( OptParameterList ) -> LambdaBody)()()
     * ;
     */
    private Expression LambdaExpression() {
        eat(TokenType.OpenParenthesis);
        if (IsLookAhead(TokenType.OpenParenthesis)) {
            var expression = LambdaExpression();
            eat(TokenType.CloseParenthesis); // eat CloseParenthesis after lambda body
            return CallExpression.of(expression, Arguments());
        }

        List<Expression> params = OptParameterList();
        eat(TokenType.CloseParenthesis);
        eat(TokenType.Lambda, "Expected -> but got: " + lookAhead().getValue());

        return LambdaExpression.of(params, LambdaBody());
    }

    private Statement LambdaBody() {
        return IsLookAhead(TokenType.OpenBraces) ? Statement() : ExpressionStatement();
    }

    /**
     * A single token lookahead recursive descent parser can’t see far enough to tell that it’s parsing an assignment
     * until after it has gone through the left-hand side and stumbled onto the =.
     * You might wonder why it even needs to. After all, we don’t know we’re parsing a + expression until
     * after we’ve finished parsing the left operand.
     * <p>
     * The difference is that the left-hand side of an assignment isn’t an expression that evaluates to a value.
     * It’s a sort of pseudo-expression that evaluates to a “thing” you can assign to. Consider:
     * {@snippet :
     * var a = "before";
     * a = "value";
     *}
     * On the second line, we don’t evaluate a (which would return the string “before”).
     * We figure out what variable a refers to so we know where to store the right-hand side expression’s value.
     * All of the expressions that we’ve seen so far that produce values are r-values.
     * An l-value “evaluates” to a storage location that you can assign into.
     * We want the syntax tree to reflect that an l-value isn’t evaluated like a normal expression.
     * That’s why the Expr.Assign node has a Token for the left-hand side, not an Expr.
     * The problem is that the parser doesn’t know it’s parsing an l-value until it hits the =.
     * In a complex l-value, that may occur many tokens later.
     * {@snippet :
     *  makeList().head.next = node;
     *}
     */
    private Expression AssignmentExpression() {
        Expression left = OrExpression();
        if (IsLookAhead(TokenType.Equal, TokenType.Equal_Complex)) {
            var operator = AssignmentOperator().getValue();
            Expression rhs = Expression();

            left = AssignmentExpression.of(isValidAssignmentTarget(left, operator), rhs, operator);
        }
        return left;
    }

    // x || y
    private Expression OrExpression() {
        var expression = AndExpression();
        while (!IsLookAhead(TokenType.EOF) && IsLookAhead(TokenType.Logical_Or)) {
            var operator = eat();
            Expression right = AndExpression();
            expression = LogicalExpression.of(operator.getValue().toString(), expression, right);
        }
        return expression;
    }

    // x && y
    private Expression AndExpression() {
        var expression = EqualityExpression();
        while (!IsLookAhead(TokenType.EOF) && IsLookAhead(TokenType.Logical_And)) {
            var operator = eat();
            Expression right = EqualityExpression();
            expression = LogicalExpression.of(operator.getValue(), expression, right);
        }
        return expression;
    }

    /**
     * x == y
     * x != y
     */
    private Expression EqualityExpression() {
        var expression = RelationalExpression();
        while (!IsLookAhead(TokenType.EOF) && IsLookAhead(TokenType.Equality_Operator)) {
            var operator = eat();
            Expression right = EqualityExpression();
            expression = BinaryExpression.of(expression, right, operator.getValue().toString());
        }
        return expression;
    }

    /**
     * x > y
     * x >= y
     * x < y
     * x <= y
     */
    private Expression RelationalExpression() {
        var expression = AdditiveExpression();
        while (!IsLookAhead(TokenType.EOF) && IsLookAhead(TokenType.RelationalOperator)) {
            var operator = eat();
            Expression right = RelationalExpression();
            expression = BinaryExpression.of(expression, right, operator.getValue().toString());
        }
        return expression;
    }

    /**
     * AssignmentOperator: +, -=, +=, /=, *=
     */
    private Token AssignmentOperator() {
        Token token = lookAhead();
        if (token.isAssignment()) {
            return eat(token.getType());
        }
        throw Error(token, "Unrecognized token");
    }

    private Expression isValidAssignmentTarget(Expression target, Object operator) {
        if (target.is(NodeType.Identifier, NodeType.MemberExpression)) {
            return target;
        }
        Object value = iterator.getCurrent().getValue();
        if (target instanceof Literal n) {
            throw Error("Invalid left-hand side in assignment expression: %s %s %s".formatted(n.getVal(), operator, value));
        } else {
            throw Error("Invalid left-hand side in assignment expression: %s %s %s".formatted(printer.eval(target), operator, value));
        }
    }

    private RuntimeException Error(String message) {
        return Error(iterator.lookAhead(), message);
    }

    private RuntimeException Error(Token token, String message) {
        return iterator.error(message, token);
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
            left = BinaryExpression.of(left, right, operator.getValue().toString());
        }

        return left;
    }

    private Expression MultiplicativeExpression() {
        var left = UnaryExpression();

        // (10*5)-5
        while (match("*", "/", "%")) {
            var operator = eat();
            Expression right = UnaryExpression();
            left = new BinaryExpression(left, right, operator.getValue().toString());
        }

        return left;
    }

    private Expression UnaryExpression() {
        var operator = switch (lookAhead().getType()) {
            case Minus -> eat(TokenType.Minus);
            case Increment -> eat(TokenType.Increment);
            case Decrement -> eat(TokenType.Decrement);
            case Logical_Not -> eat(TokenType.Logical_Not);
            default -> null;
        };
        if (operator != null) {
            return UnaryExpression.of(operator.getValue(), UnaryExpression());
        }

        return LeftHandSideExpression();
    }

    @Nullable
    private Expression PrimaryExpression() {
        if (lookAhead() == null) {
            return null;
        }
        iterator.eatLineTerminator();
        return switch (lookAhead().getType()) {
            case OpenParenthesis, OpenBrackets -> ParenthesizedExpression();
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
            case This -> ThisExpression();
            case EOF -> null;
            default -> LeftHandSideExpression();
        };
    }

    /**
     * ThisExpression
     * : this
     * ;
     */
    private Expression ThisExpression() {
        eat(TokenType.This);
        return ThisExpression.of();
    }

    private Statement ResourceDeclaration() {
        eat(TokenType.Resource);
        Identifier type = Identifier();
        Identifier name = null;
        if (IsLookAhead(TokenType.Identifier)) {
            name = Identifier();
        }
        var body = BlockExpression();

        return ResourceExpression.of(type, name, (BlockExpression) body);
    }

    private Expression LeftHandSideExpression() {
        return CallMemberExpression();
    }

    // bird.fly()
    private Expression CallMemberExpression() {
        var primaryIdentifier = MemberExpression(); // .fly
        while (true) {
            if (IsLookAhead(TokenType.OpenParenthesis)) { // fly(
                primaryIdentifier = CallExpression.of(primaryIdentifier, Arguments());
            } else {
                break;
            }
        }
        return primaryIdentifier;
    }

    private List<Expression> Arguments() {
        eat(TokenType.OpenParenthesis, "Expect '(' before arguments.");
        var list = ArgumentList();
        eat(TokenType.CloseParenthesis, "Expect ')' after arguments.");
        return list;
    }

    private List<Expression> ArgumentList() {
        if (IsLookAhead(TokenType.CloseParenthesis)) return Collections.emptyList();

        var arguments = new ArrayList<Expression>();
        do {
            if (arguments.size() >= 128) {
                throw Error(lookAhead(), "Can't have more than 128 arguments");
            }
            arguments.add(Expression());
        } while (match(TokenType.Comma) && eat(TokenType.Comma, "Expect ',' after argument: " + iterator.getCurrent().getRaw()) != null);

        return arguments;
    }

    /**
     * a.Expression
     * a[ Expression ]
     */
    private Expression MemberExpression() {
        var object = PrimaryExpression();
        for (var next = lookAhead(); IsLookAhead(TokenType.Dot, TokenType.OpenBrackets); next = lookAhead()) {
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

    private Identifier Identifier() {
        var id = eat(TokenType.Identifier);
        return new Identifier(id.getValue());
    }

    private Expression ParenthesizedExpression() {
        if (IsLookAheadAfter(TokenType.CloseParenthesis, TokenType.Lambda)) {
            return LambdaExpression();
        }
        eat(TokenType.OpenParenthesis);
        var res = Expression();
        if (IsLookAhead(TokenType.CloseParenthesis)) {
            eat(TokenType.CloseParenthesis, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
        } else if (IsLookAhead(TokenType.CloseBraces)) {
            eat(TokenType.CloseBraces, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
        }
        return res;
    }

    private Expression Literal() {
        Token current = iterator.getCurrent();
        return switch (current.getType()) {
            case True, False -> BooleanLiteral();
            case Null -> NullLiteral.of();
            case Number -> NumericLiteral.of(current.getValue());
            case String -> new StringLiteral(current.getValue());
            default -> new ErrorExpression(current.getValue());
        };
    }

    private Expression BooleanLiteral() {
//        var literal = eat();
        return BooleanLiteral.of(iterator.getCurrent().getValue());
    }

    boolean IsLookAheadAfter(TokenType after, TokenType... type) {
        return iterator.IsLookAheadAfter(after, type);
    }

    Token lookAhead() {
        return iterator.lookAhead();
    }

    Token eat() {
        return iterator.eat();
    }

    Token eat(TokenType... type) {
        return iterator.eat("Expected token: %s but it was %s".formatted(Arrays.toString(type).replaceAll("\\]?\\[?", ""), lookAhead().getRaw()), type);
    }

    Token eat(TokenType type, String error) {
        return iterator.eat(error, type);
    }

    private boolean IsLookAhead(TokenType... type) {
        return iterator.IsLookAhead(type);
    }

    private boolean match(String... strings) {
        return iterator.hasNext() && lookAhead().is(strings);
    }

    private boolean match(TokenType... strings) {
        return iterator.hasNext() && IsLookAhead(strings);
    }

}
