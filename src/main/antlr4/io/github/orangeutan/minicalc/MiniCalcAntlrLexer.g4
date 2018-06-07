lexer grammar MiniCalcAntlrLexer;

channels { WHITESPACE }

// Whitespace
NEWLINE    : '\r\n' | '\r' | '\n' ;
WS         : [\t ]+ -> channel(WHITESPACE) ;

// Keywords
INPUT      : 'input';
VAR        : 'var';
PRINT      : 'print';
AS         : 'as';
INT        : 'Int';
DECIMAL    : 'Decimal';
STRING     : 'String';

// Literals
INTLIT     : '0'|[1-9][0-9]* ;
DECLIT     : [1-9][0-9]*'.'[0-9]+ | [0-9]'.'[0-9]*;

// Operators
PLUS       : '+';
MINUS      : '-';
ASTERISK   : '*';
DIVISION   : '/';
ASSIGN     : '=';
LPAREN     : '(';
RPAREN     : ')';

// Identifiers
ID         : [_]*[a-z][A-Za-z0-9_]* ;
STR_OPEN : '"' -> pushMode(MODE_IN_STR);
UNMATCHED  : .;

// String Mode
mode MODE_IN_STR;
    ESC_STR_DELIMITER : '\\"' ;
    ESC_SLASH         : '\\\\' ;
    ESC_NEWLINE       : '\\n' ;
    ESC_SHARP         : '\\#' ;
    STR_CLOSE         : '"' -> popMode ;
    INTERP_OPEN       : '#{' -> pushMode(MODE_IN_INTERP) ;
    STR_CONTENT       : ~["\n\r\t\\#]+ ;
    STR_UNMATCH       : . -> type(UNMATCHED);

// Interpolation Mode
mode MODE_IN_INTERP;
    INTERP_CLOSE      : '}' -> popMode;
    INTERP_WS         : [\t ]+ -> skip;

    // Keywords
    INTERP_AS         : 'as'-> type(AS) ;
    INTERP_INT        : 'Int'-> type(INT) ;
    INTERP_DECIMAL    : 'Decimal'-> type(DECIMAL) ;
    INTERP_STRING     : 'String'-> type(STRING) ;

    // Literals
    INTERP_INTLIT     : ('0'|[1-9][0-9]*) -> type(INTLIT) ;
    INTERP_DECLIT     : ('0'|[1-9][0-9]*) '.' [0-9]+ -> type(DECLIT) ;

    // Operators
    INTERP_PLUS       : '+' -> type(PLUS);
    INTERP_MINUS      : '-' -> type(MINUS);
    INTERP_ASTERISK   : '*' -> type(ASTERISK);
    INTERP_DIVISION   : '/' -> type(DIVISION);
    INTERP_ASSIGN     : '=' -> type(ASSIGN);
    INTERP_LPAREN     : '(' -> type(LPAREN);
    INTERP_RPAREN     : ')' -> type(RPAREN);

    // Identifiers
    INTERP_ID         : [_]*[a-z][A-Za-z0-9_]* -> type(ID);
    INTERP_STR_OPEN   : '"' -> type(STR_OPEN), pushMode(MODE_IN_STR);
    INTERP_UNMATCHED  : . -> type(UNMATCHED) ;
