grammar Benglemsc;

@header {
    package org.antlr.generated;
}

program : statement+ ;

statement
    : varAssignment ';'             #varAssignmentStmt
    | methodCall ';'                #methodCallStmt
    ;

varAssignment
    : ID ':=' expr                  #varAssign
    ;

expr
    : 'new' ID '(' argList? ')'     #newExpr
    | ID                            #idExpr
    | INT                           #intExpr
    ;

argList
    : expr (',' expr)*
    ;

methodCall
    : ID '.' ID '(' argList? ')'    #methodCallExpr
    ;

ID  : [a-zA-Z_][a-zA-Z_0-9]* ;
INT : [0-9]+ ;
WS  : [ \t\r\n]+ -> skip ;
