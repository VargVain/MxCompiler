package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstLi extends ASMInst{
    public ASMInstLi(ASMReg rd, int imm) {
        this.rd = rd;
        this.imm = imm;
    }
    @Override
    public String toString() {
        return "li " + rd + ", " + imm;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
