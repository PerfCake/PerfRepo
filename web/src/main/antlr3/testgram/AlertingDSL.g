grammar AlertingDSL;

options {
    language = Java;
    output = AST;
}

tokens {
    ANY;
}

@lexer::header {
  package org.jboss.qa.perfrepo.web.alerting;
}

@parser::header {
  package org.jboss.qa.perfrepo.web.alerting;
}

WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };

SELECT      : 'SELECT';
DEFINE      : 'DEFINE';
CONDITION   : 'CONDITION';
WHERE       : 'WHERE';
BETWEEN     : 'BETWEEN';
AND         : 'AND';
LAST        : 'LAST';
ASSIGN      : '=';
IN          : 'IN';
AVG         : 'AVG';
MIN         : 'MIN';
MAX         : 'MAX';
NUMBER      : ('0'..'9');
ANY_CHAR    : . ;




expression : condition define EOF;


condition         : CONDITION^ any_with_equals;
define            : DEFINE^ assign_sequence;
assign_sequence   : assign  (','! assign)*;

assign            : any ASSIGN^ '('! simple_select ')'! |
                    any ASSIGN^ simple_select |
                    any ASSIGN^ avg |
                    any ASSIGN^ max |
                    any ASSIGN^ min;

avg               : AVG^ '('! multi_select ')'!;
max               : MAX^ '('! multi_select ')'!;
min               : MIN^ '('! multi_select ')'!;

simple_select     : SELECT^ simple_where;
multi_select      : SELECT^ in_where | SELECT^ between_where | SELECT^ last;
simple_where      : WHERE^ equals_condition;
in_where          : WHERE^ in_condition;
between_where     : WHERE^ between_condition;
last              : LAST^ number | LAST^ number ','! number;

equals_condition  : any ASSIGN^ any;
in_condition      : any IN^ '('! any (','! any)* ')'!;
between_condition : any BETWEEN^ any AND! any;

number            : NUMBER* -> ANY[$text];
any_with_equals   : ('=' | '(' | ')' | ANY_CHAR | NUMBER)* -> ANY[$text];
any               : (NUMBER | ANY_CHAR)* -> ANY[$text];
