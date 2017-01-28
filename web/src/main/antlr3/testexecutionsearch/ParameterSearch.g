grammar ParameterSearch;

options {
    language = Java;
    output = AST;
}

tokens {
    ANY;
}

@lexer::header {
  package org.perfrepo.web.dao.search;
}

@parser::header {
  package org.perfrepo.web.dao.search;
}

// Lexer rules - TOKENs:
WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };

AND                  : 'AND' | 'and';
OR                   : 'OR' | 'or';
LPAREN               : '(';
RPAREN               : ')';
COLON                : ':';
QUOTES               : '"';
ANY_CHAR             : . ;

// Parser rules:

query : expression EOF;
expression : or_expression;
or_expression : and_expression (OR^ and_expression)*;
and_expression : term (AND^ term)*;
term : QUOTES! any QUOTES! COLON^ QUOTES! any QUOTES!| LPAREN! expression RPAREN!;

any               : (ANY_CHAR)* -> ANY[$text];
