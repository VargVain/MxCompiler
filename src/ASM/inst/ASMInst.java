package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

abstract public class ASMInst {
    public ASMReg rd, rs1, rs2;
    public int imm = 0;
    @Override
    abstract public String toString();
    abstract public void accept(ASMVisitor visitor);
}
