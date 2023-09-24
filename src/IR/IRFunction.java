package IR;

import IR.type.IRType;
import IR.val.IRVal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class IRFunction {
    public String name;
    public IRType returnType;
    public int valCnt = 0;
    public HashMap<String, Integer> register = new HashMap<>();
    public ArrayList<IRVal> params = new ArrayList<>();
    public LinkedList<IRBasicBlock> blocks = new LinkedList<>();
    public HashSet<IRVal> allVariables = new HashSet<>();
    public IRFunction(String name, IRType returnType) {
        this.name = name;
        this.returnType = returnType;
    }
    public IRFunction() {}
    public IRBasicBlock newBlock(String string) {
        IRBasicBlock block = new IRBasicBlock(string);
        blocks.add(block);
        return block;
    }
    public IRBasicBlock newBlock(IRBasicBlock block) {
        blocks.add(block);
        return block;
    }
    public int register(String name) {
        if (!register.containsKey(name)) {
            register.put(name, 0);
            return 0;
        }
        else {
            int old = register.get(name);
            register.put(name, ++old);
            return old;
        }
    }
    @Override
    public String toString() {
        String ret = "define " + returnType.toString() + " @" + name + "(";
        for (int i = 0; i < params.size(); ++i) {
            ret += params.get(i).toString();
            if (i != params.size() - 1) ret += ", ";
        }
        ret += ") {\n";
        for (IRBasicBlock block : blocks)
            ret += block.toString();
        ret += "}\n";
        return ret;
    }
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
