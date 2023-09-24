package IR.inst;

import IR.IRVisitor;
import IR.val.IRTemp;
import IR.val.IRVal;

public class IRInstBitcast extends IRInst{
    public IRTemp dest;
    public IRVal val;
    public IRInstBitcast(IRTemp dest, IRVal val) {
        this.dest = dest;
        this.val = val;
    }
    @Override
    public String toString() {
        return dest.Name() + " = bitcast " + val + " to " + dest.type;
    }
    @Override
    public void replaceUse(IRVal oldVal, IRVal newVal) {
        if (val == oldVal) val = newVal;
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
