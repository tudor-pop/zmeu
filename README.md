| Name       | Operators | Associates |
|------------|-----------|------------|
| Assignment |           |            |
| Equality   | == !=     | Left       |
| Comparison | > >= < <= | Left       |
| Term       | - +       | Left       |
| Factor     | / *       | Left       |
| Unary      | ! -       | Right      |

```
Code like this is weird, so C, Java, and friends all disallow it.
 It’s as if there are two levels of “precedence” for statements. 
 Some places where a statement is allowed—like inside a block or 
 at the top level—allow any kind of statement, including declarations. 
 Others allow only the “higher” precedence statements that don’t declare names.
 
 Each rule here only matches expressions at its precedence level or higher
 For example, unary matches a unary expression like !negated or 
 a primary expression like 1234.
 And term can match 1 + 2 but also 3 * 4 / 5. 
 The final primary rule covers the highest-precedence forms—literals 
 and parenthesized expressions.
Program        → Declaration* EOF 

# Distinction rule for statements that declare names
Declaration     → VarDeclaration
                | Statement 
                | FunDeclaration
                | TypeDeclaration
                | ResourceDeclaration


TypeDeclaration → "type" Identifier "{" BlockExpression "}"
               
ResourceDeclaration  →  resource Identifier Identifier? "{" Assignment* "}"  

FunDeclaration  → "fun" Identifier "(" Parameters? ")" BlockExpression

Parameters      → Identifier ( "," Identifier )*
                
VarDeclaration  → "var" IDENTIFIER ( "=" expression )? newLine 

Statement       → ExpressionStatement
                | IfStatement
                | IterationStatement
                | BlockExpression
                | ReturnStatement

ReturnStatement → "return" Expression? ;

IterationStatement  → WhileStatement
                    | ForStatement

WhileStatement  → "while" "(" Expression ")" Statement
ForStatement    → "for" "(" VarDeclaration ; Expression ; Statement

BlockExpression → "{" Declaration* "}" 

IfStatement     → "if" "(" Expression ")" Statement
                ( "else" Statement )? 
                
Expression      → Assignment 

Assignment      → IDENTIFIER "=" Assignment
                | OrExpression 

OrExpression    → AndExpression ("or" AndExpression)*

AndExpression   → Equality ("and" Equality)*               

Equality        → Comparison ( ( "!=" | "==" ) Comparison )*   

Comparison      → Term ( ( ">" | ">=" | "<" | "<=" ) Term )*   

Term            → Factor ( ( "-" | "+" ) Factor )*   

Factor          → Unary ( ( "/" | "*" ) Unary )*   

Unary           → ( "!" | "-" ) Unary  
                | CallExpression

CallExpression  → Primary ("(" Arguments? ")")*            

Arguments       → Expression ("," Expression)*
                
Primary         → NUMBER | STRING | "true" | "false" | "nil"  
                | "(" Expression ")" 
                | IDENTIFIER
                
newLine         →  | \n

```

# Error handling

We're using the **Panic Mode** as the error recovery mechanism. As soon as the parser detects an error, it enters panic
mode. It knows at least one token doesn’t make sense given its current state in the middle of some stack of grammar
productions.

Before it can get back to parsing, it needs to get its state and the sequence of
forthcoming tokens aligned such that the next token does match the rule being parsed.
This process is called **synchronization**.

To do that, we select some rule in the grammar that will mark the synchronization point.
The parser fixes its parsing state by jumping out of any nested productions until it gets
back to that rule. Then it synchronizes the token stream by discarding tokens until it
reaches one that can appear at that point in the rule.

The traditional place in the grammar to synchronize is between statements