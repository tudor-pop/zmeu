
```
Code like this is weird, so C, Java, and friends all disallow it.
 It’s as if there are two levels of “precedence” for statements. 
 Some places where a statement is allowed—like inside a block or 
 at the top level—allow any kind of statement, including declarations. 
 Others allow only the “higher” precedence statements that don’t declare names.
 
program        → declaration* EOF ;

# Distinction rule for statements that declare names
declaration    → variableDeclaration
                | statement ;
                
varDecl        → "var" IDENTIFIER ( "=" expression )? newLine ;

statement      → exprStmt
                | printStmt ;

expression     → equality ;  
equality       → comparison ( ( "!=" | "==" ) comparison )* ;  
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;  
term           → factor ( ( "-" | "+" ) factor )* ;  
factor         → unary ( ( "/" | "*" ) unary )* ;  
unary          → ( "!" | "-" ) unary  
                | primary ;  
primary        → NUMBER | STRING | "true" | "false" | "nil"  
                | "(" expression ")" ;
                
newLine        → ; | \n

```