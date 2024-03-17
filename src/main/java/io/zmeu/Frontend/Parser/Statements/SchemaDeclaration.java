package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Expressions.PathIdentifier;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class SchemaDeclaration extends Statement {
    private PathIdentifier name;
    private Statement body;

    public SchemaDeclaration(PathIdentifier name, @Nullable Statement body) {
        this();
        this.name = name;
        this.body = body;
    }

    public SchemaDeclaration(PathIdentifier name, @Nullable Expression body) {
        this(name, ExpressionStatement.of(body));
    }

    public SchemaDeclaration() {
        this.kind = NodeType.SchemaDeclaration;
    }

    public static Statement of(PathIdentifier test, Expression body) {
        return new SchemaDeclaration(test, body);
    }

    public static Statement of(PathIdentifier test, Statement body) {
        return new SchemaDeclaration(test, body);
    }

    public static Statement of(PathIdentifier test, int value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(PathIdentifier test, double value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new SchemaDeclaration();
    }

    public static Statement of(PathIdentifier test, float value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(PathIdentifier test, String value) {
        return new SchemaDeclaration(test, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
