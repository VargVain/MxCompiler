package IR.inst;

import IR.type.IRType;
import IR.type.IRTypePtr;
import IR.val.IRVal;

public class IRInstStore extends IRInst{
    public IRVal val, to;
    public IRInstStore(IRVal val, IRVal to) {
        this.val = val;
        this.to = to;
    }
    @Override
    public String toString() {
        if (val.type == irNullType) return "store " + ((IRTypePtr) to.type).PtrToType() + " null, " + to;
        else return "store " + val + ", " + to;
    }
}
