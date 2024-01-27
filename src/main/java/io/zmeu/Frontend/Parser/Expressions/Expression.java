package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.NodeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;

@ToString
@EqualsAndHashCode
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
