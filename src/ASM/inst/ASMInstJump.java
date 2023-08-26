package ASM.inst;

import ASM.ASMBlock;
import ASM.ASMVisitor;

public class ASMInstJump extends ASMInst{
    public ASMBlock to;
    public ASMInstJump(ASMBlock to) {
        this.to = to;
    }
    @Override
    public String toString() {
        return "j " + to.name;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
