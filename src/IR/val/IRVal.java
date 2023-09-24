package IR.val;

import ASM.register.ASMReg;
import IR.IRBasicBlock;
import IR.type.IRType;
import util.Local;

import java.util.HashSet;

abstract public class IRVal implements Local {
    public IRType type;
    public ASMReg asmReg;
    public IRVal(IRType type) {
        this.type = type;
    }
    public HashSet<IRBasicBlock> defSites = new HashSet<>();
    abstract public String toString();
    abstract public String Name();
}
