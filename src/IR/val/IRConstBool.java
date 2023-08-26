package IR.val;

import IR.type.IRType;

public class IRConstBool extends IRConst{
    public Boolean val;
    public IRConstBool(Boolean val) {
        super(irBoolType);
        this.val = val;
    }
    @Override
    public String toString() {
        return val ? "i1 1" : "i1 0";
    }
    @Override
    public String Name() {return val ? "1" : "0";}
}
