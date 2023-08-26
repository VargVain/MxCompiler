package ASM.inst;

import ASM.ASMVisitor;

public class ASMInstCall extends ASMInst{
    public String funcName;
    public ASMInstCall(String funcName) {
        this.funcName = funcName;
    }
    @Override
    public String toString() {
        return "call " + funcName;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
