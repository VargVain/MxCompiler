package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstMove extends ASMInst{
    public ASMInstMove(ASMReg rd, ASMReg rs) {
        this.rd = rd;
        this.rs1 = rs;
    }
    @Override
    public String toString() {
        return "mv " + rd + ", " + rs1;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
