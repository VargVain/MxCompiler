package util;

import AST.*;

import java.util.HashMap;

public class ClassMembers{
    public HashMap<String, DefFuncNode> functions = new HashMap<>();
    public HashMap<String, DefVarUnitNode> variables = new HashMap<>();
    public void putFunc(String name, DefFuncNode node) {
        functions.put(name, node);
    }
    public DefFuncNode getFunc(String name) {
        return functions.get(name);
    }
    public boolean hasFunc(String name) {
        return functions.containsKey(name);
    }
    public void putVar(String name, DefVarUnitNode node) {
        variables.put(name, node);
    }
    public DefVarUnitNode getVar(String name) {
        return variables.get(name);
    }
    public boolean hasVar(String name) {
        return variables.containsKey(name);
    }
    public ClassMembers() {}
}
