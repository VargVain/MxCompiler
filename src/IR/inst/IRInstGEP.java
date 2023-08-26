package IR.inst;

import IR.IRVisitor;
import IR.type.IRTypePtr;
import IR.val.IRTemp;
import IR.val.IRVal;

public class IRInstGEP extends IRInst{
    public IRTemp dest;
    public IRVal ptr, index1, index2;
    public IRInstGEP(IRTemp dest, IRVal ptr, IRVal index1) {
        this.dest = dest;
        this.ptr = ptr;
        this.index1 = index1;
    }
    public IRInstGEP(IRTemp dest, IRVal ptr, IRVal index1, IRVal index2) {
        this.dest = dest;
        this.ptr = ptr;
        this.index1 = index1;
        this.index2 = index2;
    }
    @Override
    public String toString() {
        if (index2 == null) {
            return dest.Name() + " = getelementptr " + ((IRTypePtr)ptr.type).PtrToType() + ", " + ptr + ", " + index1;
        } else
            return dest.Name() + " = getelementptr " + ((IRTypePtr)ptr.type).PtrToType() + ", " + ptr + ", " + index1 + ", " + index2;
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
