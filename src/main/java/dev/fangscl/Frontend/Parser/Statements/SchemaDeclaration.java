package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

/**
 * SchemaDeclaration
 * : schema Identifier BlockStatement
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SchemaDeclaration extends Statement {
    private Identifier name;
    private Statement body;

    public SchemaDeclaration(Identifier name, @Nullable Statement body) {
        this();
        this.name = name;
        this.body = body;
    }
    public SchemaDeclaration(Identifier name, @Nullable Expression body) {
        this(name,ExpressionStatement.of(body));
    }

    public SchemaDeclaration() {
        this.kind = NodeType.SchemaDeclaration;
    }

    public static Statement of(Identifier test, Expression body) {
        return new SchemaDeclaration(test, body);
    }

    public static Statement of(Identifier test, Statement body) {
        return new SchemaDeclaration(test, body);
    }

    public static Statement of(Identifier test, int value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, double value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new SchemaDeclaration();
    }

    public static Statement of(Identifier test, float value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, String value) {
        return new SchemaDeclaration(test, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
