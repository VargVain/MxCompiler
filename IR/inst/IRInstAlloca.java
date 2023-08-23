package IR.inst;

import IR.type.IRType;
import IR.val.IRVal;

public class IRInstAlloca extends IRInst{
    public IRVal val;
    public IRType type;
    public IRInstAlloca(IRVal val, IRType type) {
        this.val = val;
        this.type = type;
    }
    @Override
    public String toString() {
        return val.Name() + " = alloca " + type.toString();
    }
}
