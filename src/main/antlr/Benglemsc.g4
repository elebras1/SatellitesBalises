grammar Benglemsc;

program : statement+ ;

statement : 'print' expr ';'  #print
    | 'let' ID '=' expr ';' #assignment
    ;

expr : expr ('+'|'-') expr #addSub
    | INT #int
    | ID #id
    ;

ID  : [a-zA-Z_][a-zA-Z_0-9]* ;
INT : [0-9]+ ;
WS  : [ \t\r\n]+ -> skip ;