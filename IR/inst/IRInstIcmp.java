package IR.inst;

import IR.val.IRTemp;
import IR.val.IRVal;

public class IRInstIcmp extends IRInst{
    public IRTemp dest;
    public String op;
    public IRVal lhs, rhs;
    public IRInstIcmp(IRTemp dest, String op, IRVal lhs, IRVal rhs) {
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    @Override
    public String toString() {
        return dest.Name() + " = icmp " + op + " " + lhs + ", " + rhs.Name();
    }
}
