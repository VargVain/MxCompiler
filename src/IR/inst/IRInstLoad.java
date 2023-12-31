package IR.inst;

import IR.IRVisitor;
import IR.val.IRVal;
import IR.val.IRTemp;

public class IRInstLoad extends IRInst{
    public IRTemp dest;
    public IRVal from;
    public IRInstLoad(IRTemp dest, IRVal from) {
        this.dest = dest;
        this.from = from;
    }
    @Override
    public String toString() {
        return dest.Name() + " = load " + dest.type + ", " + from;
    }
    @Override
    public void replaceUse(IRVal oldVal, IRVal newVal) {
        if (from == oldVal) from = newVal;
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
