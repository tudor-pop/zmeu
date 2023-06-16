package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Literals.Identifier;
import dev.fangscl.Frontend.Parser.Literals.NumericLiteral;
import dev.fangscl.Frontend.Parser.Literals.StringLiteral;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class TypeDeclaration extends Statement {
    private Identifier name;
    private Statement body;

    public TypeDeclaration(Identifier name, @Nullable Statement body) {
        this();
        this.name = name;
        this.body = body;
    }

    public TypeDeclaration(Identifier name, @Nullable Expression body) {
        this(name, ExpressionStatement.of(body));
    }

    public TypeDeclaration() {
        this.kind = NodeType.SchemaDeclaration;
    }

    public static Statement of(Identifier test, Expression body) {
        return new TypeDeclaration(test, body);
    }

    public static Statement of(Identifier test, Statement body) {
        return new TypeDeclaration(test, body);
    }

    public static Statement of(Identifier test, int value) {
        return new TypeDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, double value) {
        return new TypeDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of() {
        return new TypeDeclaration();
    }

    public static Statement of(Identifier test, float value) {
        return new TypeDeclaration(test, NumericLiteral.of(value));
    }

    public static Statement of(Identifier test, String value) {
        return new TypeDeclaration(test, StringLiteral.of(value));
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
