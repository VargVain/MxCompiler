package ASM;

import ASM.inst.ASMInst;
import ASM.register.ASMReg;

import java.util.HashSet;
import java.util.LinkedList;

public class ASMBlock {
    public String name;
    public static int blockCnt = 0;
    public LinkedList<ASMInst> instructions = new LinkedList<>();
    public LinkedList<ASMInst> phis = new LinkedList<>();
    public LinkedList<ASMBlock> succ = new LinkedList<>();
    public HashSet<ASMReg> liveIn = new HashSet<ASMReg>(), liveOut = new HashSet<ASMReg>();
    public HashSet<ASMReg> use = new HashSet<ASMReg>(), def = new HashSet<ASMReg>();
    public ASMBlock() {
        this.name = ".L" + blockCnt++;
    }
    public void addInst(ASMInst inst) {
        instructions.add(inst);
    }
    @Override
    public String toString() {
        String ret = "";
        if (name != null) ret += name + ":\n";
        // ret += "liveOut: " + liveOut;
        for (ASMInst inst : instructions)
            ret += "  " + inst + "\n";
        return ret;
    }
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
