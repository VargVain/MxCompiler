package IR.inst;

import IR.val.IRTemp;
import IR.val.IRVal;

public class IRInstBitcast extends IRInst{
    IRTemp dest;
    IRVal val;
    public IRInstBitcast(IRTemp dest, IRVal val) {
        this.dest = dest;
        this.val = val;
    }
    @Override
    public String toString() {
        return dest.Name() + " = bitcast " + val + " to " + dest.type;
    }
}
