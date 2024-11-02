package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.Literals.SymbolIdentifier;
import io.zmeu.Frontend.Parser.Literals.TypeIdentifier;
import io.zmeu.Frontend.Parser.NodeType;
import io.zmeu.Frontend.Parser.Statements.BlockExpression;
import io.zmeu.Frontend.Parser.Statements.Statement;
import io.zmeu.Runtime.Interpreter;
import io.zmeu.Runtime.Values.DeferredObserverValue;
import io.zmeu.Visitors.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceExpression extends Statement implements DeferredObserverValue {
    private Identifier type;
    @Nullable
    private Identifier name;
    private BlockExpression block;
    private boolean isEvaluated;

    private ResourceExpression() {
        this.kind = NodeType.ResourceExpression;
        this.name = new SymbolIdentifier();
    }

    private ResourceExpression(Identifier type, Identifier name, BlockExpression block) {
        this();
        this.type = type;
        this.name = name;
        this.block = block;
    }

    public static Statement resource(Identifier type, Identifier name, BlockExpression block) {
        return new ResourceExpression(type, name, block);
    }

    public static Statement resource() {
        return new ResourceExpression();
    }
    public static Statement resource(TypeIdentifier type, Identifier name, BlockExpression block) {
        return new ResourceExpression(type, name, block);
    }

    public List<Statement> getArguments() {
        return block.getExpression();
    }

    public String name() {
        return name.string();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }


    @Override
    public void update(Interpreter interpreter) {
        interpreter.eval(this);
    }

}
