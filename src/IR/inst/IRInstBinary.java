package IR.inst;

import IR.IRVisitor;
import IR.val.IRTemp;
import IR.val.IRVal;

public class IRInstBinary extends IRInst{
    public IRTemp dest;
    public String op;
    public IRVal lhs, rhs;
    public IRInstBinary(IRTemp dest, String op, IRVal lhs, IRVal rhs) {
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    @Override
    public String toString() {
        return dest.Name() + " = " + op + " " + lhs + ", " + rhs.Name();
    }
    @Override
    public void replaceUse(IRVal oldVal, IRVal newVal) {
        if (lhs == oldVal) lhs = newVal;
        if (rhs == oldVal) rhs = newVal;
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
