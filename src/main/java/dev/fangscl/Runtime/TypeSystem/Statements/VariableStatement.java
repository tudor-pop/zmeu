package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Expressions.VariableDeclaration;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * BlockStatement
 * : 'var' Identity [, Identity] Assignment Expression
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class VariableStatement extends Statement {
    private List<VariableDeclaration> declarations;

    public VariableStatement(@Nullable List<VariableDeclaration> declarations) {
        this.kind = NodeType.VariableStatement;
        this.declarations = declarations;
    }

    public VariableStatement() {
        this(Collections.emptyList());
    }

    public static Statement of(VariableDeclaration... expression) {
        return new VariableStatement(List.of(expression));
    }
    public static Statement of(List<VariableDeclaration> expression) {
        return new VariableStatement(expression);
    }

    public static Statement of() {
        return new VariableStatement();
    }

    @Override
    public String toSExpression() {
        return ArrayUtils.toString(declarations.stream().map(Statement::toSExpression).toList());
    }
}
