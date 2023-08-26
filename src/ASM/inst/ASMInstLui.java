package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstLui extends ASMInst{
    public String globalAddr = null;
    public ASMInstLui(ASMReg rd, int imm) {
        this.rd = rd;
        this.imm = imm;
    }
    public ASMInstLui(ASMReg rd, String globalAddr) {
        this.rd = rd;
        this.globalAddr = globalAddr;
    }
    @Override
    public String toString() {
        if (globalAddr != null) return "lui " + rd + ", " + globalAddr;
        return "lui " + rd + ", " + imm;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
