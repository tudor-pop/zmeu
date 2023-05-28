package dev.fangscl.Frontend.Parser.Statements;

import dev.fangscl.Frontend.Parser.Expressions.VariableDeclaration;
import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * VariableStatement
 * : var Identity (, Identity)* Assignment Expression
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class VariableStatement extends Statement {
    private List<VariableDeclaration> declarations;

    private VariableStatement(@Nullable List<VariableDeclaration> declarations) {
        this.kind = NodeType.VariableStatement;
        this.declarations = declarations;
    }

    private VariableStatement() {
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

}
