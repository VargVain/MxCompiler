package IR.inst;

import IR.IRBasicBlock;
import IR.IRVisitor;
import IR.val.IRVal;

public class IRInstJump extends IRInst{
    public IRBasicBlock to;
    public IRInstJump(IRBasicBlock to) {
        this.to = to;
    }
    @Override
    public String toString() {
        return "br label %" + to.name;
    }

    @Override
    public void replaceUse(IRVal oldVal, IRVal newVal) {}

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
