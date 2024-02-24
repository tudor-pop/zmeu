package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.Expression;
import io.zmeu.Frontend.Parser.Expressions.PackageIdentifier;
import io.zmeu.Frontend.Parser.Literals.NumericLiteral;
import io.zmeu.Frontend.Parser.Literals.StringLiteral;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class SchemaDeclaration extends Statement {
    private PackageIdentifier name;
    private Statement body;

    public SchemaDeclaration(PackageIdentifier name, @Nullable Statement body) {
        this();
        this.name = name;
        this.body = body;
    }

    public SchemaDeclaration(PackageIdentifier name, @Nullable Expression body) {
        this(name, ExpressionStatement.of(body));
    }

    public SchemaDeclaration() {
        this.kind = NodeType.SchemaDeclaration;
    }

    public static Statement of(PackageIdentifier test, Expression body) {
        return new SchemaDeclaration(test, body);
    }

    public static Statement of(PackageIdentifier test, Statement body) {
        return new SchemaDeclaration(test, body);
    }

    public static Statement of(PackageIdentifier test, int value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(PackageIdentifier test, double value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new SchemaDeclaration();
    }

    public static Statement of(PackageIdentifier test, float value) {
        return new SchemaDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(PackageIdentifier test, String value) {
        return new SchemaDeclaration(test, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
