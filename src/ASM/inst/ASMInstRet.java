package ASM.inst;

import ASM.ASMVisitor;

public class ASMInstRet extends ASMInst{
    @Override
    public String toString() {
        return "ret";
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
