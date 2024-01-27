package io.zmeu.Frontend.Parser.Statements;

import io.zmeu.Frontend.Parser.NodeType;
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
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
