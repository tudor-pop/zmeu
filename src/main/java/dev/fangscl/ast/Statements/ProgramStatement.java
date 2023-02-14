package dev.fangscl.ast.Statements;

import dev.fangscl.ast.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProgramStatement extends Statement {
    private List<Statement> body;

    public ProgramStatement() {
        this(new ArrayList<>());
    }
    public ProgramStatement(List<Statement> body) {
        this.kind = NodeType.Program;
        this.body = body;
    }

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }
}
