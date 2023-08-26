package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstUnary extends ASMInst{
    String op;
    Boolean hasImm = true;
    public ASMInstUnary(String op, ASMReg rd, ASMReg rs1) {
        this.op = op;
        this.rd = rd;
        this.rs1 = rs1;
        hasImm = false;
    }
    public ASMInstUnary(String op, ASMReg rd, ASMReg rs1, int imm) {
        this.op = op;
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }
    @Override
    public String toString() {
        if (!hasImm) return op + " " + rd + ", " + rs1;
        else return op + " " + rd + ", " + rs1 + ", " + imm;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
