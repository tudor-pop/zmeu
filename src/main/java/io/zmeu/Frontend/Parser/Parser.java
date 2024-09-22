package io.zmeu.Frontend.Parser;

import io.zmeu.ErrorSystem;
import io.zmeu.Frontend.Lexer.Token;
import io.zmeu.Frontend.Lexer.TokenType;
import io.zmeu.Frontend.Parser.Expressions.*;
import io.zmeu.Frontend.Parser.Literals.*;
import io.zmeu.Frontend.Parser.Statements.*;
import io.zmeu.Frontend.Parser.Types.TypeParser;
import io.zmeu.Frontend.Parser.Types.ValueType;
import io.zmeu.Frontend.TypeChecker.TypeChecker;
import io.zmeu.Frontend.visitors.SyntaxPrinter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.zmeu.Frontend.Lexer.TokenType.*;
import static io.zmeu.Frontend.Parser.Literals.ParameterIdentifier.param;
import static io.zmeu.Frontend.Parser.Statements.ExpressionStatement.expressionStatement;


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
    private TypeChecker typeChecker = new TypeChecker();
    private TypeParser typeParser = new TypeParser(this);

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
//            if (iterator.getCurrent().isLineTerminator()) { // if we eat too much - going beyond lineTerminator -> go back 1 token
//                iterator.prev();
//            }
            statementList.add(statement);
            if (IsLookAhead(endTokenType) || iterator.getCurrent().type() == EOF) {
                // after some work is done, before calling iterator.next(),
                // we must check for EOF again or else we risk going outside the iterators bounds
                break;
            }

        }

        return statementList;
    }

    private Statement Declaration() {
        try {
            return switch (lookAhead().type()) {
                case Fun -> FunctionDeclaration();
                case Schema -> SchemaDeclaration();
                case Resource -> ResourceDeclaration();
                case Module -> ModuleDeclaration();
                case Var -> VariableDeclarations();
                default -> Statement();
            };
        } catch (RuntimeException error) {
            ErrorSystem.error(error.toString());
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
        return switch (lookAhead().type()) {
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
        return switch (lookAhead().type()) {
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
        return switch (lookAhead().type()) {
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
        return expressionStatement(Expression());
    }

    /**
     * BlockStatement
     * : { Statements? }
     * ;
     * Statements
     * : Statement* Expression
     */
    private Expression BlockExpression() {
        return BlockExpression(null, "Error");
    }

    private Expression BlockExpression(String errorOpen, String errorClose) {
        eat(OpenBraces, errorOpen);
        var res = IsLookAhead(CloseBraces)
                ? BlockExpression.block(Collections.emptyList())
                : BlockExpression.block(StatementList(CloseBraces));
        if (IsLookAhead(CloseBraces)) { // ? { } => eat } & return the block
            eat(CloseBraces, errorClose);
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
        var init = IsLookAhead(lineTerminator, Comma, EOF) ? null : VariableInitializer();
        return VariableDeclaration.of(id, type, init);
    }

    /**
     * TypeDeclaration
     * : (':' PathIdentifier)?
     * ;
     */
    public TypeIdentifier TypeDeclaration() {
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
        return switch (lookAhead().type()) {
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
        return IfStatement.If(test, ifBlock, elseBlock);
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
        var type = typeParser.FunctionType();

        Statement body = ExpressionStatement.expressionStatement(BlockExpression());
        return FunctionDeclaration.fun(test, params, type, body);
    }

    private Statement SchemaDeclaration() {
        eat(Schema);
        var packageIdentifier = Identifier();

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

        Statement body = ExpressionStatement.expressionStatement(BlockExpression());
        return InitStatement.of(params, body);
    }

    private List<ParameterIdentifier> OptParameterList() {
        return IsLookAhead(CloseParenthesis) ? Collections.emptyList() : ParameterList();
    }

    /**
     * ParameterList
     * : Identifier
     * | ParameterList, Identifier
     * ;
     */
    private List<ParameterIdentifier> ParameterList() {
        var params = new ArrayList<ParameterIdentifier>();
        do {
            params.add(FunParameter());
        } while (IsLookAhead(Comma) && eat(Comma) != null);

        return params;
    }

    private ParameterIdentifier FunParameter() {
        var symbol = SymbolIdentifier();
        if (IsLookAhead(Identifier, Colon)) {
            var type = TypeDeclaration();
            return param(symbol, type);
        } else {
            return param(symbol);
        }
    }

    private Statement ReturnStatement() {
        eat(Return);
        var arg = OptExpression();
        return ReturnStatement.funReturn(arg);
    }

    private Expression OptExpression() {
        return IsLookAhead(lineTerminator) ? TypeIdentifier.type(ValueType.Void) : Expression();
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
            return CallExpression.call(expression, Arguments());
        }

        var params = OptParameterList();
        eat(CloseParenthesis);
        eat(Lambda, "Expected -> but got: " + lookAhead().value());

        return LambdaExpression.lambda(params, LambdaBody());
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
            var operator = AssignmentOperator().value();
            Expression rhs = Expression();

            left = AssignmentExpression.assign(isValidAssignmentTarget(left, operator), rhs, operator);
        }
        return left;
    }

    // x || y
    private Expression OrExpression() {
        var expression = AndExpression();
        while (!IsLookAhead(EOF) && IsLookAhead(Logical_Or)) {
            var operator = eat();
            Expression right = AndExpression();
            expression = LogicalExpression.of(operator.value().toString(), expression, right);
        }
        return expression;
    }

    // x && y
    private Expression AndExpression() {
        var expression = EqualityExpression();
        while (!IsLookAhead(EOF) && IsLookAhead(Logical_And)) {
            var operator = eat();
            Expression right = EqualityExpression();
            expression = LogicalExpression.of(operator.value(), expression, right);
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
            expression = BinaryExpression.binary(expression, right, operator.value().toString());
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
            expression = BinaryExpression.binary(expression, right, operator.value().toString());
        }
        return expression;
    }

    /**
     * AssignmentOperator: +, -=, +=, /=, *=
     */
    private Token AssignmentOperator() {
        Token token = lookAhead();
        if (token.isAssignment()) {
            return eat(token.type());
        }
        throw Error(token, "Unrecognized token");
    }

    private Expression isValidAssignmentTarget(Expression target, Object operator) {
        if (target.is(NodeType.Identifier, NodeType.MemberExpression)) {
            return target;
        }
        Object value = iterator.getCurrent().value();
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
            left = BinaryExpression.binary(left, right, operator.value().toString());
        }

        return left;
    }

    private Expression MultiplicativeExpression() {
        var left = UnaryExpression();

        // (10*5)-5
        while (match("*", "/", "%")) {
            var operator = eat();
            Expression right = UnaryExpression();
            left = new BinaryExpression(left, right, operator.value().toString());
        }

        return left;
    }

    private Expression UnaryExpression() {
        var operator = switch (lookAhead().type()) {
            case Minus -> eat(Minus);
            case Increment -> eat(Increment);
            case Decrement -> eat(Decrement);
            case Logical_Not -> eat(Logical_Not);
            default -> null;
        };
        if (operator != null) {
            return UnaryExpression.of(operator.value(), UnaryExpression());
        }

        return LeftHandSideExpression();
    }

    @Nullable
    private Expression PrimaryExpression() {
        if (lookAhead() == null) {
            return null;
        }
        return switch (lookAhead().type()) {
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
     * : resource TypeIdentifier name '{'
     * :    VariableDeclaration
     * : '}'
     * ;
     */
    private Statement ResourceDeclaration() {
        eat(Resource);
        var type = TypeIdentifier();
        Identifier name = null;
        if (IsLookAhead(TokenType.Identifier)) {
            name = Identifier();
        }
        var body = BlockExpression("Expect '{' after resource name.", "Expect '}' after resource body.");

        return ResourceExpression.resource(type, name, (BlockExpression) body);
    }


    /**
     * ModuleDeclaration
     * : module TypeIdentifier name '{'
     * :    Inputs
     * : '}'
     * ;
     */
    private Statement ModuleDeclaration() {
        eat(Module);
        var moduleType = PluginIdentifier();
        var name = Identifier();
        var body = BlockExpression("Expect '{' after module name.", "Expect '}' after module body.");

        return ModuleExpression.of((PluginIdentifier) moduleType, name, (BlockExpression) body);
    }

    private Expression LeftHandSideExpression() {
        return CallMemberExpression();
    }

    // bird.fly()
    private Expression CallMemberExpression() {
        var primaryIdentifier = MemberExpression(); // .fly
        while (true) {
            if (IsLookAhead(OpenParenthesis)) { // fly(
                primaryIdentifier = CallExpression.call(primaryIdentifier, Arguments());
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
        } while (match(Comma) && eat(Comma, "Expect ',' after argument: " + iterator.getCurrent().raw()) != null);

        return arguments;
    }

    /**
     * a.Expression
     * a[ Expression ]
     */
    private Expression MemberExpression() {
        var object = PrimaryExpression();
        for (var next = lookAhead(); IsLookAhead(Dot, OpenBrackets); next = lookAhead()) {
            object = switch (next.type()) {
                case Dot -> {
                    var property = MemberProperty();
                    yield MemberExpression.member(false, object, property);
                }
                case OpenBrackets -> {
                    var property = MemberPropertyIndex();
                    yield MemberExpression.member(true, object, property);
                }
                default -> throw new IllegalStateException("Unexpected value: " + next.type());
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

    private PluginIdentifier PluginIdentifier() {
        switch (lookAhead().type()) {
            case String -> {
                var token = eat(String);
                return PluginIdentifier.fromString(token.value().toString());
            }
            case Identifier -> {
                TypeIdentifier type = TypeIdentifier();
                return PluginIdentifier.from(type);
            }
            case null, default -> throw new RuntimeException("Unexpected token type: " + lookAhead().type());
        }
    }

    /**
     * Parse Type with prefix
     * Base.Nested
     */
    @NotNull
    public TypeIdentifier TypeIdentifier() {
        var type = new StringBuilder();
        for (var next = eat(TokenType.Identifier); ; next = eat(TokenType.Identifier)) {
            switch (lookAhead().type()) {
                case Dot -> {
                    type.append(next.value().toString());
                    type.append(".");
                    eat(Dot);
                }
                case Colon -> { // :Type just eat and move on
                    eat(Colon);
                }
                case null -> {
                }
                default -> {
                    type.append(next.value().toString());
                    return TypeIdentifier.type(type.toString());
                }
            }
        }
    }

    private Identifier Identifier() {
        return switch (lookAhead().type()) {
            case String -> TypeIdentifier();
            default -> SymbolIdentifier();
        };
    }

    private @NotNull SymbolIdentifier SymbolIdentifier() {
        var id = eat(TokenType.Identifier);
        return new SymbolIdentifier(id.value());
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
        return switch (current.type()) {
            case True, False -> BooleanLiteral();
            case Null -> NullLiteral.of();
            case Number -> NumberLiteral.of(current.value());
            case String -> new StringLiteral(current.value());
            default -> new ErrorExpression(current.value());
        };
    }

    private Expression BooleanLiteral() {
//        var literal = eat();
        return BooleanLiteral.of(iterator.getCurrent().value());
    }

    boolean IsLookAheadAfter(TokenType after, TokenType... type) {
        return iterator.IsLookAheadAfter(after, type);
    }

    public Token lookAhead() {
        return iterator.lookAhead();
    }

    public Token eat() {
        return iterator.eat();
    }

    public Token eat(TokenType... type) {
        return iterator.eat("Expected token: %s but it was %s".formatted(Arrays.toString(type).replaceAll("\\]?\\[?", ""), lookAhead().raw()), type);
    }

    public Token eat(TokenType type, String error) {
        return iterator.eat(error, type);
    }

    public boolean IsLookAhead(List<TokenType> list, TokenType... types) {
        return IsLookAhead(list) || IsLookAhead(types);
    }

    public boolean IsLookAhead(TokenType... type) {
        return iterator.IsLookAhead(type);
    }

    public boolean IsLookAhead(List<TokenType> type) {
        for (TokenType p : type) {
            if (iterator.IsLookAhead(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean match(String... strings) {
        return iterator.hasNext() && lookAhead().is(strings);
    }

    public boolean match(TokenType... strings) {
        return iterator.hasNext() && IsLookAhead(strings);
    }

}
