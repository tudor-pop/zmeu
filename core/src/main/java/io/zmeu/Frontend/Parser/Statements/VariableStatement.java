package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.Expressions.VariableDeclaration;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Visitors.Visitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static io.zmeu.Frontend.Parser.Expressions.VariableDeclaration.*;

/**
 * <p>
 * VariableStatement
 * : var Identity (, Identity)* Assignment Expression
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public final class VariableStatement extends Statement {
    private List<VariableDeclaration> declarations;

    private VariableStatement(@Nullable List<VariableDeclaration> declarations) {
        this.declarations = declarations;
    }

    private VariableStatement() {
        this(Collections.emptyList());
    }

    public static Statement of(VariableDeclaration... expression) {
        return new VariableStatement(List.of(expression));
    }

    public static Statement statement(VariableDeclaration... expression) {
        return new VariableStatement(List.of(expression));
    }

    public static Statement statement(Identifier expression) {
        return new VariableStatement(List.of(var(expression)));
    }

    public static Statement of(List<VariableDeclaration> expression) {
        return new VariableStatement(expression);
    }

    public static Statement of() {
        return new VariableStatement();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
