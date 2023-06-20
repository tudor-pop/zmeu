package dev.fangscl.Frontend.Parser.Expressions;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;

@ToString
public abstract class Expression {

    @Getter
    @Setter
    protected NodeType kind;

    public boolean is(NodeType type) {
        return kind == type;
    }

    public boolean is(NodeType... type) {
        return ArrayUtils.contains(type, kind);
    }


    public abstract <R> R accept(Visitor<R> visitor);

}
