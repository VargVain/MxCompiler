package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstLoad extends ASMInst{
    int size;
    public ASMInstLoad(int size, ASMReg rd, ASMReg rs1, int imm) {
        this.size = size;
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }
    @Override
    public String toString() {
        return "l" + (size == 1 ? "b" : "w") + " " + rd + ", " + imm + "(" + rs1 + ")";
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
