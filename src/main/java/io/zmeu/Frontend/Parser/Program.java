package io.zmeu.Frontend.Parser;

import io.zmeu.Frontend.Parser.Statements.Statement;
import io.zmeu.Visitors.Visitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class Program extends Statement {
    private List<Statement> body;

    public Program() {
        this(new ArrayList<>());
    }

    public Program(List<Statement> body) {
        this.kind = NodeType.Program;
        this.body = body;
    }

    public static Program of(Statement... body) {
        return new Program(Arrays.stream(body).toList());
    }

    public static Program program(Statement... body) {
        return new Program(Arrays.stream(body).toList());
    }

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }

    public void add(Statement statement) {
        this.addStatement(statement);
    }

    public Statement first() {
        return body.listIterator().next();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public boolean hasBody() {
        return body != null;
    }
}