package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstBinary extends ASMInst{
    public String op;
    public ASMInstBinary(String op, ASMReg rd, ASMReg rs1, ASMReg rs2) {
        this.op = op;
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        switch (op) {
            case "sdiv" -> this.op = "div";
            case "srem" -> this.op = "rem";
            case "shl" -> this.op = "sll";
            case "ashr" -> this.op = "sra";
        }
    }
    @Override
    public String toString() {
        return op + " " + rd + ", " + rs1 + ", " + rs2;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
