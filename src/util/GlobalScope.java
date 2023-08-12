package util;

import AST.*;

import java.util.HashMap;

public class GlobalScope extends Scope implements Local{
    public HashMap<String, DefFuncNode> functions = new HashMap<String, DefFuncNode>();
    public HashMap<String, DefClassNode> classes = new HashMap<String, DefClassNode>();
    public void putFunc(String name, DefFuncNode node) {
        functions.put(name, node);
    }
    public DefFuncNode getFunc(String name) {
        return functions.get(name);
    }
    public boolean hasFunc(String name) {
        return functions.containsKey(name);
    }
    public void putClass(String name, DefClassNode node) {
        classes.put(name, node);
    }
    public DefClassNode getClass(String name) {
        return classes.get(name);
    }
    public boolean hasClass(String name) {
        return classes.containsKey(name);
    }
    public GlobalScope() {
        
        functions.put("print", PrintFunc);
        functions.put("println", PrintlnFunc);
        functions.put("printInt", PrintIntFunc);
        functions.put("printlnInt", PrintlnIntFunc);
        functions.put("getString", GetStringFunc);
        functions.put("getInt", GetIntFunc);
        functions.put("toString", ToStringFunc);

        DefClassNode myString = new DefClassNode(null, "string");
        myString.classMembers.putFunc("length", StringLengthFunc);
        myString.classMembers.putFunc("substring", StringSubStringFunc);
        myString.classMembers.putFunc("parseInt", StringParseIntFunc);
        myString.classMembers.putFunc("ord", StringOrdFunc);
        classes.put("string", myString);
        classes.put("int", new DefClassNode(null, "int"));
        classes.put("bool", new DefClassNode(null, "bool"));
    }
}
