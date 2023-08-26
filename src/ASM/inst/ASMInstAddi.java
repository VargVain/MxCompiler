package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstAddi extends ASMInst{
    public String globalAddr = null;
    public ASMInstAddi(ASMReg rd, ASMReg rs1, int imm) {
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }
    public ASMInstAddi(ASMReg rd, ASMReg rs1, String globalAddr) {
        this.rd = rd;
        this.rs1 = rs1;
        this.globalAddr = globalAddr;
    }
    @Override
    public String toString() {
        if (globalAddr != null) return "addi " + rd + ", " + rs1 + ", " + globalAddr;
        return "addi " + rd + ", " + rs1 + ", " + imm;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
