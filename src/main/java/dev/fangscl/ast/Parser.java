package dev.fangscl.ast;

import dev.fangscl.ast.statements.*;
import dev.fangscl.ast.statements.expressions.DecimalExpression;
import dev.fangscl.ast.statements.expressions.Expression;
import dev.fangscl.ast.statements.expressions.IdentifierExpression;
import dev.fangscl.ast.statements.expressions.IntegerExpression;
import dev.fangscl.lexer.Lexer;
import dev.fangscl.lexer.Token;
import dev.fangscl.lexer.TokenType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/*
 * Responsability: take the tokens from the lexer and create an AST
 *
 * */
@Data
@Log4j2
public class Parser {
    private List<Token> tokens;
    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public ProgramStatement produceAST(String src) {
        var program = new ProgramStatement();

        tokens = lexer.tokenize(src);

        for (var token : tokens) {
            if (token.getType() == TokenType.EOF) {
                break;
            }
            program.addStatement(this.parseExpression(token));
        }

        return program;
    }

    private Expression parseExpression(Token token) {
        return switch (token.getType()) {
            case Identifier -> new IdentifierExpression(token.getValue());
            case Integer -> new IntegerExpression(token.getValue());
            case Decimal -> new DecimalExpression(token.getValue());
            default -> new Expression();
        };
    }
}
