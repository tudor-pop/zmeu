package io.zmeu.Frontend.Parser;

import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.TokenType;
import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.Parser.errors.InvalidTypeInitException;
import io.zmeu.Frontend.visitors.SyntaxPrinter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.zmeu.Frontend.Lexer.TokenType.*;


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
        var statements = StatementList(EOF);
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
            ErrorSystem.error(error.getMessage());
            iterator.synchronize();
            return null;
        }
    }

    /**
     * {@snippet
             *: Statement
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
        eat(While);
        eat(OpenParenthesis);
        var test = Expression();
        eat(CloseParenthesis);
        if (IsLookAhead(NewLine)) {
            /* while(x)
             *   x=2
             */
            eat(NewLine);
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
        eat(For);
        eat(OpenParenthesis);

        Statement init = ForStatementInit();
        eat(SemiColon);

        var test = ForStatementTest();
        eat(SemiColon);

        var update = ForStatementIncrement();
        eat(CloseParenthesis);
        if (IsLookAhead(NewLine)) {
            /* while(x)
             *   x=2
             */
            eat(NewLine);
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
        return IsLookAhead(CloseParenthesis) ? null : Expression();
    }

    @Nullable
    private Expression ForStatementTest() {
        return IsLookAhead(SemiColon) ? null : Expression();
    }

    private Statement ForStatementInit() {
        return switch (lookAhead().getType()) {
            case Var -> {
                eat(Var);
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
        eat(Var);
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
        eat(OpenBraces);
        var res = IsLookAhead(CloseBraces)
                ? BlockExpression.of(Collections.emptyList())
                : BlockExpression.of(StatementList(CloseBraces));
        if (IsLookAhead(CloseBraces)) { // ? { } => eat } & return the block
            eat(CloseBraces, "Error");
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
        } while (IsLookAhead(Comma) && eat(Comma) != null);
        return declarations;
    }

    /**
     * VariableDeclaration
     * : Identifier (:TypeDeclaration)? VariableInitialization?
     * ;
     */
    private VariableDeclaration VariableDeclaration() {
        var id = Identifier();
        var type = TypeDeclaration();
        var init = IsLookAhead(lineTerminator(), Comma, EOF) ? null : VariableInitializer();
        if (type != null && init != null) { // if type is declared
            if (init instanceof Literal literal) {
                if (!StringUtils.equals(type.getSymbol(), literal.type().name())) {
                    throw new InvalidTypeInitException(type.getSymbol(), literal.type().name(), literal.getVal());
                }
            }
        }
        return VariableDeclaration.of(id, type, init);
    }

    /**
     * TypeDeclaration
     * : (':' TokenType.Number | TokenType.String)
     */
    private PackageIdentifier TypeDeclaration() {
        if (IsLookAhead(Colon)) {
            eat(Colon);

            return TypeIdentifier();
        } else {
            return null;
        }
    }

    /**
     * VariableInitializer
     * : SIMPLE_ASSIGN Expression
     */
    private Expression VariableInitializer() {
        if (IsLookAhead(Equal, Equal_Complex)) {
            eat(Equal, Equal_Complex);
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
        eat(If);
        eat(OpenParenthesis);
        var test = Expression();
        eat(CloseParenthesis);
        if (IsLookAhead(NewLine)) {
            // if(x) x=2
            eat(NewLine);
        }

        Statement ifBlock = Statement();
        Statement elseBlock = ElseStatement();
        return IfStatement.of(test, ifBlock, elseBlock);
    }

    private Statement ElseStatement() {
        if (IsLookAhead(Else)) {
            eat(Else);
            Statement alternate = Statement();
            iterator.eatIf(CloseBraces);
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
        eat(Fun, "Fun token expected: " + lookAhead());
        var test = Identifier();
        eat(OpenParenthesis, "Expected '(' but got: " + lookAhead());
        var params = OptParameterList();
        eat(CloseParenthesis, "Expected ')' but got: " + lookAhead());

        Statement body = ExpressionStatement.of(BlockExpression());
        return FunctionDeclaration.of(test, params, body);
    }

    private Statement SchemaDeclaration() {
        eat(Schema);
        var packageIdentifier = TypeIdentifier();

        Expression body = BlockExpression();
        return SchemaDeclaration.of(packageIdentifier, body);
    }

    /**
     * InitStatement
     * : init  ( OptParameterList ) BlockStatement?
     * ;
     */
    private Statement InitStatement() {
        eat(Init);
        eat(OpenParenthesis);
        var params = OptParameterList();
        eat(CloseParenthesis);

        Statement body = ExpressionStatement.of(BlockExpression());
        return InitStatement.of(params, body);
    }

    private List<Identifier> OptParameterList() {
        return IsLookAhead(CloseParenthesis) ? Collections.emptyList() : ParameterList();
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
        } while (IsLookAhead(Comma) && eat(Comma) != null);

        return params;
    }

    private Statement ReturnStatement() {
        eat(Return);
        var arg = OptExpression();
        iterator.eatLineTerminator();
        return ReturnStatement.of(arg);
    }

    private Expression OptExpression() {
        return IsLookAhead(lineTerminator()) ? null : Expression();
    }

    /**
     * LambdaExpression
     * : ( OptParameterList ) -> LambdaBody
     * | (( OptParameterList ) -> LambdaBody)()()
     * ;
     */
    private Expression LambdaExpression() {
        eat(OpenParenthesis);
        if (IsLookAhead(OpenParenthesis)) {
            var expression = LambdaExpression();
            eat(CloseParenthesis); // eat CloseParenthesis after lambda body
            return CallExpression.of(expression, Arguments());
        }

        var params = OptParameterList();
        eat(CloseParenthesis);
        eat(Lambda, "Expected -> but got: " + lookAhead().getValue());

        return LambdaExpression.of(params, LambdaBody());
    }

    private Statement LambdaBody() {
        return IsLookAhead(OpenBraces) ? Statement() : ExpressionStatement();
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
        if (IsLookAhead(Equal, Equal_Complex)) {
            var operator = AssignmentOperator().getValue();
            Expression rhs = Expression();

            left = AssignmentExpression.of(isValidAssignmentTarget(left, operator), rhs, operator);
        }
        return left;
    }

    // x || y
    private Expression OrExpression() {
        var expression = AndExpression();
        while (!IsLookAhead(EOF) && IsLookAhead(Logical_Or)) {
            var operator = eat();
            Expression right = AndExpression();
            expression = LogicalExpression.of(operator.getValue().toString(), expression, right);
        }
        return expression;
    }

    // x && y
    private Expression AndExpression() {
        var expression = EqualityExpression();
        while (!IsLookAhead(EOF) && IsLookAhead(Logical_And)) {
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
        while (!IsLookAhead(EOF) && IsLookAhead(Equality_Operator)) {
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
        while (!IsLookAhead(EOF) && IsLookAhead(RelationalOperator)) {
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
        return ErrorSystem.error(message, token);
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
            case Minus -> eat(Minus);
            case Increment -> eat(Increment);
            case Decrement -> eat(Decrement);
            case Logical_Not -> eat(Logical_Not);
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
        eat(This);
        return ThisExpression.of();
    }

    /**
     * ResourceDeclaration
     * : resource Type name '{'
     * :    VariableDeclaration
     * : '}'
     * ;
     */
    private Statement ResourceDeclaration() {
        eat(Resource);
        Identifier type = Identifier();
        Identifier name = null;
        if (IsLookAhead(TokenType.Identifier)) {
            name = Identifier();
        }
        eat(OpenBraces, "Expect '{' after resource name.");
        var body = new ArrayList<Statement>();
        while (!IsLookAhead(CloseBraces)) {
            if (IsLookAhead(lineTerminator())) {
                eat(lineTerminator());
                continue;
            }
            body.add(ExpressionStatement.of(AssignmentExpression()));
        }
        eat(CloseBraces, "Expect '}' after resource body.");

        return ResourceExpression.of(type, name, (BlockExpression) BlockExpression.of(body));
    }

    private PackageIdentifier TypeIdentifier() {
        var identifier = new PackageIdentifier();
        for (var next = eat(TokenType.Identifier);/* IsLookAhead(TokenType.Dot, TokenType.OpenBraces, TokenType.AT, TokenType.lineTerminator(), EOF)*/ ; next = eat(TokenType.Identifier)) {
            switch (lookAhead().getType()) {
                case Dot -> {
                    identifier.addPackage(next.getValue().toString());
                    eat(Dot);
                }
                case AT -> {
                    throw new NotImplementedException("case not implemented");
                }
                default -> {
                    identifier.setType(next.getValue().toString());
                    return identifier;
                }
            }
        }
    }

    private Expression LeftHandSideExpression() {
        return CallMemberExpression();
    }

    // bird.fly()
    private Expression CallMemberExpression() {
        var primaryIdentifier = MemberExpression(); // .fly
        while (true) {
            if (IsLookAhead(OpenParenthesis)) { // fly(
                primaryIdentifier = CallExpression.of(primaryIdentifier, Arguments());
            } else {
                break;
            }
        }
        return primaryIdentifier;
    }

    private List<Expression> Arguments() {
        eat(OpenParenthesis, "Expect '(' before arguments.");
        var list = ArgumentList();
        eat(CloseParenthesis, "Expect ')' after arguments.");
        return list;
    }

    private List<Expression> ArgumentList() {
        if (IsLookAhead(CloseParenthesis)) return Collections.emptyList();

        var arguments = new ArrayList<Expression>();
        do {
            if (arguments.size() >= 128) {
                throw Error(lookAhead(), "Can't have more than 128 arguments");
            }
            arguments.add(Expression());
        } while (match(Comma) && eat(Comma, "Expect ',' after argument: " + iterator.getCurrent().getRaw()) != null);

        return arguments;
    }

    /**
     * a.Expression
     * a[ Expression ]
     */
    private Expression MemberExpression() {
        var object = PrimaryExpression();
        for (var next = lookAhead(); IsLookAhead(Dot, OpenBrackets); next = lookAhead()) {
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
        eat(OpenBrackets);
        var property = Expression();
        eat(CloseBrackets);
        return property;
    }

    private Expression MemberProperty() {
        eat(Dot);
        return Identifier();
    }

    private Identifier Identifier() {
        var id = eat(TokenType.Identifier);
        return new Identifier(id.getValue());
    }

    private Expression ParenthesizedExpression() {
        if (IsLookAheadAfter(CloseParenthesis, Lambda)) {
            return LambdaExpression();
        }
        eat(OpenParenthesis);
        var res = Expression();
        if (IsLookAhead(CloseParenthesis)) {
            eat(CloseParenthesis, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
        } else if (IsLookAhead(CloseBraces)) {
            eat(CloseBraces, "Unexpected token found inside parenthesized expression. Expected closed parenthesis.");
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
