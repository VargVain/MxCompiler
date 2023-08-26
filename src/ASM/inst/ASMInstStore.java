package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstStore extends ASMInst{
    int size;
    public ASMInstStore(int size, ASMReg rs1, ASMReg rs2, int imm) {
        this.size = size;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
    }
    @Override
    public String toString() {
        return "s" + (size == 1 ? "b" : "w") + " " + rs2 + ", " + imm + "(" + rs1 + ")";
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
