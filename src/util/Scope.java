package util;

import AST.DefClassNode;
import AST.DefVarUnitNode;
import IR.IRBasicBlock;
import IR.val.IRVal;
import IR.val.IRVariable;

import java.util.HashMap;

public class Scope {
    public HashMap<String, DefVarUnitNode> members = new HashMap<String, DefVarUnitNode>();
    public HashMap<String, Integer> register = new HashMap<>();
    public HashMap<String, IRVal> ValRegister = new HashMap<>();
    public IRBasicBlock breakTo, continueTo;
    public String register(String name, int ord) {
        register.put(name, ord);
        return "%" + name + "." + ord;
    }
    public String getRegister(String name) {
        int ord = register.get(name);
        return "%" + name + "." + ord;
    }
    public void putIRVal(String name, IRVariable val) {
        ValRegister.put(name, val);
    }
    public IRVal getIRVal(String name) {
        return ValRegister.get(name);
    }
    public IRVal searchIRVal(String name) {
        if (ValRegister.containsKey(name)) {
            return ValRegister.get(name);
        }
        else {
            return parentScope != null ? parentScope.searchIRVal(name) : null;
        }
    }
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
        this.breakTo = parentScope.breakTo;
        this.continueTo = parentScope.continueTo;
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
