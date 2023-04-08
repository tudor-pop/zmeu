package dev.fangscl.Runtime.TypeSystem.Statements;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * EmptyStatement
 * : '\n'
 * ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmptyStatement extends Statement {

    public EmptyStatement() {
        this.kind = NodeType.EmptyStatement;
    }

    public static Statement of() {
        return new EmptyStatement();
    }

    @Override
    public String toSExpression() {
        return  "" ;
    }
}
