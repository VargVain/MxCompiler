lexer grammar MxLexer;

// Keywords
Void: 'void';
Bool: 'bool';
Int: 'int';
String: 'string';
New: 'new';
Class: 'class';
Null: 'null';
True: 'true';
False: 'false';
This: 'this';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';

// Operators
LeftParen: '(';
RightParen: ')';
LeftBracket: '[';
RightBracket: ']';
LeftBrace: '{';
RightBrace: '}';
Question: '?';
Colon: ':';
Semi: ';';
Comma: ',';

OpLt: '<';
OpLeq: '<=';
OpGt: '>';
OpGeq: '>=';
OpShl: '<<';
OpShr: '>>';

OpAdd: '+';
OpSub: '-';
OpMul: '*';
OpDiv: '/';
OpMod: '%';
OpIncre: '++';
OpDecre: '--';

OpAnd: '&';
OpOr: '|';
OpAndAnd: '&&';
OpOrOr: '||';
OpCaret: '^';
OpNot: '!';
OpTilde: '~';

OpEq: '==';
OpNeq: '!=';
OpAssign: '=';
OpMemberAccess: '.';

// Identifier
Identifier
    : [a-zA-Z] [a-zA-Z_0-9]*
    ;

// Literals
LogicalLiteral
    : True | False;
IntegerLiteral
    : [1-9] [0-9]*
    | '0'
    ;
fragment EscapeCharacter
    : 'n'
    | '\\'
    | '"';
fragment StringCharacter
    : ~["\\\n\r\u2028\u2029]
    | '\\' EscapeCharacter;
StringLiteral
    : '"' StringCharacter* '"';
NullLiteral
    : Null;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

// Comments
BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;