package IR;

import ASM.ASMBlock;
import IR.inst.IRInst;
import IR.*;
import IR.inst.IRInstPhi;
import IR.val.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class IRBasicBlock {
    public String name;
    public static int blockCnt = 0;
    public ASMBlock asmBlock = null;
    public LinkedList<IRInst> instructions = new LinkedList<>();
    public LinkedList<IRBasicBlock> succ = new LinkedList<>(), pred = new LinkedList<>();
    public IRBasicBlock idom;
    public LinkedList<IRBasicBlock> domFrontier = new LinkedList<>();
    public LinkedList<IRBasicBlock> children = new LinkedList<>();
    public HashSet<IRVal> orig = new HashSet<>();
    public HashMap<IRVal, IRTemp> phiMap = new HashMap<>();
    public LinkedList<IRInstPhi> phis = new LinkedList<>();
    public IRBasicBlock(String string) {
        this.name = string + "_" + blockCnt++;
    }
    public void addInst(IRInst inst) {
        instructions.add(inst);
    }
    @Override
    public String toString() {
        String ret = name + ":\n";
        // ret += "phi: " + phiMap.toString() + "\n";
        // ret += "var: " + orig.toString() + "\n";
        for (IRInst inst : instructions)
            ret += "  " + inst + "\n";
        return ret;
    }
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
