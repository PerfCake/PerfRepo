grammar AlertingDSL;

options {
    language = Java;
    output = AST;
}

tokens {
    ANY;
}

@lexer::header {
  package org.perfrepo.web.alerting;
}

@parser::header {
  package org.perfrepo.web.alerting;
}

WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };

SELECT               : 'SELECT';
DEFINE               : 'DEFINE';
CONDITION            : 'CONDITION';
WHERE                : 'WHERE';
AND                  : 'AND';
LAST                 : 'LAST';
ASSIGN               : '=';
LTE                  : '<=';
GTE                  : '>=';
IN                   : 'IN';
AVG                  : 'AVG';
MIN                  : 'MIN';
MAX                  : 'MAX';
NUMBER_NOT_ONE       : ('0' | '2'..'9');
ONE                  : '1';
ANY_CHAR             : . ;




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

simple_select     : SELECT^ equals_where simple_last? | SELECT^ simple_last;

multi_select      : SELECT^ equals_where multi_last? |
                    SELECT^ in_where |
                    SELECT^ multi_last;

equals_where      : WHERE^ equals_condition (AND! equals_condition)*;
in_where          : WHERE^ in_condition;
simple_last       : LAST^ ONE;
multi_last        : LAST^ number | LAST^ number ','! number;

equals_condition  : any ASSIGN^ any |
                    any ASSIGN^ '"'! any '"'! |
                    any LTE^ any |
                    any LTE^ '"'! any '"'! |
                    any GTE^ any |
                    any GTE^ '"'! any '"'!;

in_condition      : any IN^ '('! any (','! any)* ')'!;

number            : (NUMBER_NOT_ONE | ONE)* -> ANY[$text];
any_with_equals   : ('=' | '(' | ')' | ANY_CHAR | NUMBER_NOT_ONE | ONE)* -> ANY[$text];
any               : (NUMBER_NOT_ONE | ONE | ANY_CHAR)* -> ANY[$text];
