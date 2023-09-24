package IR.inst;

import IR.IRVisitor;
import IR.type.IRType;
import IR.type.IRTypeInt;
import IR.val.IRConstInt;
import IR.val.IRConstNull;
import IR.val.IRVal;
import util.Local;

abstract public class IRInst implements Local {
    abstract public String toString();
    abstract public void replaceUse(IRVal oldVal, IRVal newVal);
    abstract public void accept(IRVisitor visitor);
    public IRVal defaultVal(IRType type) {
        if (type instanceof IRTypeInt) return new IRConstInt(0);
        else return new IRConstNull(type);
    }
}
