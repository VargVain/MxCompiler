parser grammar MxParser;

options {
  tokenVocab = MxLexer;
}

program: (funcDef | classDef | varDef)* EOF;

funcDef
  : returnType Identifier '(' parameterList? ')' '{' suite '}';
returnType
  : type | Void;
parameterList
  : (type Identifier) (',' type Identifier)*;

suite: statement*;

classDef
  : Class Identifier '{' (varDef | classBuild | funcDef)* '}' ';';
classBuild
  : Identifier '(' ')' '{' suite '}';

varDef
  : type varDefUnit (',' varDefUnit)* ';';
varDefUnit
  : Identifier ('=' expr)?;
type: typeName ('[' ']')*;
typeName: baseType | Identifier;
baseType: Int | Bool | String;

statement
  : '{' suite '}'
  | varDef
  | ifStmt | whileStmt | forStmt
  | breakStmt | continueStmt | returnStmt
  | exprStmt;

ifStmt
  : If '(' expr ')' statement (Else statement)?;
whileStmt
  : While '(' expr ')' statement;
forStmt
  : For '(' forInit exprStmt expr? ')' statement;
forInit
  : varDef | exprStmt;

breakStmt: Break ';';
continueStmt: Continue ';';
returnStmt: Return expr? ';';

exprStmt: expr? ';';
expr
  : '(' expr ')'                                      #parenExpr
  | New typeName (newArrayUnit)* ('(' ')')?           #newExpr
  | expr op='.' Identifier                            #memberExpr
  | expr '[' expr ']'                                 #arrayExpr
  | expr '(' exprList? ')'                            #funcExpr
  | <assoc=right> expr op=('++' | '--')               #unaryExpr
  | op=('++' | '--') expr                             #preAddExpr
  | <assoc=right> op=('!' | '~' | '+' | '-') expr     #unaryExpr
  | expr op=('*' | '/' | '%') expr                    #binaryExpr
  | expr op=('+' | '-') expr                          #binaryExpr
  | expr op=('<<' | '>>') expr                        #binaryExpr
  | expr op=('<' | '>' | '<=' | '>=') expr            #binaryExpr
  | expr op=('==' | '!=') expr                        #binaryExpr
  | expr op='&' expr                                  #binaryExpr
  | expr op='^' expr                                  #binaryExpr
  | expr op='|' expr                                  #binaryExpr
  | expr op='&&' expr                                 #binaryExpr
  | expr op='||' expr                                 #binaryExpr
  | <assoc=right> expr op='=' expr                    #assignExpr
  | <assoc=right> expr op='?' expr op=':' expr        #condExpr
  | primary                                           #atomExpr
  ;

newArrayUnit: '[' expr? ']';

primary
  : IntegerLiteral | StringLiteral | True | False | Null
  | Identifier
  | This
  ;

exprList: expr (',' expr)*;