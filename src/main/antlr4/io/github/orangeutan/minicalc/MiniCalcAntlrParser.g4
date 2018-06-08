parser grammar MiniCalcAntlrParser;

// We specify which lexer we are using: so it knows which terminals we can use
options { tokenVocab=MiniCalcAntlrLexer; }

miniCalcFile : lines=line+;

line : statement (NEWLINE|EOF);

statement : inputDeclaration # InputDeclarationStatement
          | varDeclaration   # VarDeclarationStatement
          | assignment       # AssignmentStatement
          | print            # PrintStatement;

print : PRINT LPAREN expression RPAREN;

inputDeclaration : INPUT type name=ID;

varDeclaration : VAR assignment;

assignment : ID ASSIGN expression;

expression : left=expression operator=(DIVISION|ASTERISK) right=expression # BinOp
           | left=expression operator=(PLUS|MINUS) right=expression # BinOp
           | value=expression AS targetType=type # TypeConversion
           | LPAREN expression RPAREN  # ParenExpr
           | ID # SymbolRef
           | MINUS expression # NegExpr
           | STR_OPEN (parts+=stringLiteralContent)* STR_CLOSE # StrLit
           | INTLIT # IntLit
           | DECLIT # DecLit;

stringLiteralContent : STR_CONTENT # StrLitConstContent
                     | INTERP_OPEN expression INTERP_CLOSE # StrLitInterpContent;

type : INT # IntType
     | DECIMAL  # DecType
     | STRING # StrType;