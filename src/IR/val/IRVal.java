package IR.val;

import ASM.register.ASMReg;
import IR.type.IRType;
import util.Local;

abstract public class IRVal implements Local {
    public IRType type;
    public ASMReg asmReg;
    public IRVal(IRType type) {
        this.type = type;
    }
    abstract public String toString();
    abstract public String Name();
}
