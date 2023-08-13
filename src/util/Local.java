package util;

import AST.DefFuncNode;

public interface Local {
    DefFuncNode PrintFunc = new DefFuncNode(null, "print", "void", "string", 1);
    DefFuncNode PrintlnFunc = new DefFuncNode(null, "println", "void", "string", 1);
    DefFuncNode PrintIntFunc = new DefFuncNode(null, "printInt", "void", "int", 1);
    DefFuncNode PrintlnIntFunc = new DefFuncNode(null, "printlnInt", "void", "int", 1);
    DefFuncNode GetStringFunc = new DefFuncNode(null, "getString", "string", null, 0);
    DefFuncNode GetIntFunc = new DefFuncNode(null, "getInt", "int", null, 0);
    DefFuncNode ToStringFunc = new DefFuncNode(null, "toString", "string", "int", 1);
    DefFuncNode StringLengthFunc = new DefFuncNode(null, "length", "int", null, 0);
    DefFuncNode StringSubStringFunc = new DefFuncNode(null, "substring", "string", "int", 2);
    DefFuncNode StringParseIntFunc = new DefFuncNode(null, "parseInt", "int", null, 0);
    DefFuncNode StringOrdFunc = new DefFuncNode(null, "ord", "int", "int", 1);
    DefFuncNode ArraySizeFunc = new DefFuncNode(null, "size", "int", null, 0);
    Type VoidType = new Type("void", 0);
    Type IntType = new Type("int", 0);
    Type BoolType = new Type("bool", 0);
    Type StringType = new Type("string", 0);
    Type NullType = new Type("null", 0);
    Type ThisType = new Type("this", 0);
}
