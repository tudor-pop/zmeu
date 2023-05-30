package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public abstract class Expression {

    protected NodeType kind;

    public boolean is(NodeType type) {
        return kind == type;
    }

    public boolean is(NodeType... type) {
        return ArrayUtils.contains(type, kind);
    }


    public abstract <R> R accept(Visitor<R> visitor);

}
