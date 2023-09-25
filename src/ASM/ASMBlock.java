package ASM;

import ASM.inst.ASMInst;

import java.util.LinkedList;

public class ASMBlock {
    public String name;
    public static int blockCnt = 0;
    public LinkedList<ASMInst> instructions = new LinkedList<>();
    public LinkedList<ASMInst> phis = new LinkedList<>();
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
        for (ASMInst inst : instructions)
            ret += "  " + inst + "\n";
        return ret;
    }
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
