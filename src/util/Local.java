package util;

import AST.DefFuncNode;
import IR.type.*;

public interface Local {
    DefFuncNode PrintFunc = new DefFuncNode(null, "print", "void", "string", 1);
    DefFuncNode PrintlnFunc = new DefFuncNode(null, "println", "void", "string", 1);
    DefFuncNode PrintIntFunc = new DefFuncNode(null, "printInt", "void", "int", 1);
    DefFuncNode PrintlnIntFunc = new DefFuncNode(null, "printlnInt", "void", "int", 1);
    DefFuncNode GetStringFunc = new DefFuncNode(null, "getString", "string", null, 0);
    DefFuncNode GetIntFunc = new DefFuncNode(null, "getInt", "int", null, 0);
    DefFuncNode ToStringFunc = new DefFuncNode(null, "toString", "string", "int", 1);
    DefFuncNode StringLengthFunc = new DefFuncNode(null, "length", "int", null, 0, "string");
    DefFuncNode StringSubStringFunc = new DefFuncNode(null, "substring", "string", "int", 2, "string");
    DefFuncNode StringParseIntFunc = new DefFuncNode(null, "parseInt", "int", null, 0, "string");
    DefFuncNode StringOrdFunc = new DefFuncNode(null, "ord", "int", "int", 1, "string");
    DefFuncNode ArraySizeFunc = new DefFuncNode(null, "size", "int", null, 0);
    Type VoidType = new Type("void", 0);
    Type IntType = new Type("int", 0);
    Type BoolType = new Type("bool", 0);
    Type StringType = new Type("string", 0);
    Type NullType = new Type("null", 0);
    Type ThisType = new Type("this", 0);

    // Builtin elements for IR

    IRType irIntType = new IRTypeInt(32);
    IRType irIntPtrType = new IRTypePtr(irIntType);
    IRType irCharType = new IRTypeInt(8);
    IRType irStringType = new IRTypePtr(irCharType);
    IRType irBoolType = new IRTypeInt(1);
    IRType irNullType = new IRTypePtr();
    IRType irVoidType = new IRTypeVoid();
}
