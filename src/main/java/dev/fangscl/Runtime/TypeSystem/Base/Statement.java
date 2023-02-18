package dev.fangscl.Runtime.TypeSystem.Base;

import dev.fangscl.Runtime.TypeSystem.NodeType;
import lombok.Data;

/*
* Statements do not return a values as opposed to expressions
* example:
* var x = 10 -> does not return a value, most repl return undefined
* x = 10 -> no longer a statement is an assignment expression which returns 10
* */
@Data
public class Statement {
    protected NodeType kind;
}

