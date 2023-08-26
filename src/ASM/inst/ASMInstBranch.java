package ASM.inst;

import ASM.ASMBlock;
import ASM.ASMVisitor;
import ASM.register.ASMReg;

public class ASMInstBranch extends ASMInst{
    public ASMBlock to;
    public ASMInstBranch(ASMReg reg, ASMBlock to) {
        this.rs1 = reg;
        this.to = to;
    }
    @Override
    public String toString() {
        return "beqz " + rs1 + ", " + to.name;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
