package dev.fangscl.Runtime.TypeSystem;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Base.Statement;
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

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }

    public void add(Statement statement) {
        this.addStatement(statement);
    }

    public Statement first() {
        return body.listIterator().next();
    }
}
