package IR.val;

import IR.type.IRType;

public class IRConstNull extends IRConst{
    public IRConstNull() {
        super(irNullType);
    }
    public IRConstNull(IRType type) {
        super(type);
    }
    @Override
    public String toString() {
        if (type == irVoidType) return "void";
        if (type == irNullType) return "null";
        return type + " null";
    }
    @Override
    public String Name() {return "null";}
}
