parser grammar MiniCalcParser;

// We specify which lexer we are using: so it knows which terminals we can use
options { tokenVocab=MiniCalcLexer; }

miniCalcFile : lines=line+;

line : statement (NEWLINE|EOF);

statement : inputDeclaration # inputDeclarationStatement
          | varDeclaration   # varDeclarationStatement
          | assignment       # assignmentStatement
          | print            # printStatement;

print : PRINT LPAREN expression RPAREN;

inputDeclaration : INPUT type name=ID;

varDeclaration : VAR assignment;

assignment : ID ASSIGN expression;

expression : left=expression operator=(DIVISION|ASTERISK) right=expression # binOp
           | left=expression operator=(PLUS|MINUS) right=expression # binOp
           | value=expression AS targetType=type # typeConversion
           | LPAREN expression RPAREN  # parenExpr
           | ID # varRef
           | MINUS expression # negExpr
           | STR_OPEN (parts+=stringLiteralContent)* STR_CLOSE # strLit
           | INTLIT # intLit
           | DECLIT # decLit;

stringLiteralContent : STR_CONTENT # strLitConstContent
                     | INTERP_OPEN expression INTERP_CLOSE # strLitInterpContent;

type : INT # int
     | DECIMAL  # dec
     | STRING # str;