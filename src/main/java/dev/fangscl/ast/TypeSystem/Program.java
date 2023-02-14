package dev.fangscl.ast.TypeSystem;

import dev.fangscl.ast.TypeSystem.Base.Statement;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
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

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }
}
