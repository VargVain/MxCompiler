package util;

import AST.DefClassNode;
import AST.DefVarUnitNode;

import java.util.HashMap;

public class Scope {
    public HashMap<String, DefVarUnitNode> members = new HashMap<String, DefVarUnitNode>();
    public Scope parentScope;
    public Type returnType;
    public boolean returned = false;
    public boolean looped = false;
    public DefClassNode parentClass = null;
    public Scope() {}
    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
        this.looped = parentScope.looped;
        this.parentClass = parentScope.parentClass;
    }
    public void putVar(String name, DefVarUnitNode var) {
        members.put(name, var);
    }
    public DefVarUnitNode getVar(String name) {
        return members.get(name);
    }
    public boolean hasVar(String name) {
        return members.containsKey(name);
    }
    public Type searchVar(String name) {
        if (members.containsKey(name))
            return members.get(name).type;
        else
            return parentScope != null ? parentScope.searchVar(name) : null;
    }
}
