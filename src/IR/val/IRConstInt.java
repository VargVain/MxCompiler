package IR.val;

import IR.type.IRType;

public class IRConstInt extends IRConst{
    public int val;
    public IRConstInt(int val) {
        super(irIntType);
        this.val = val;
    }
    @Override
    public String toString() {
        return type + " " + val;
    }
    @Override
    public String Name() {return String.valueOf(val);}
}
