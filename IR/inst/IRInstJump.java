package IR.inst;

import IR.IRBasicBlock;

public class IRInstJump extends IRInst{
    public IRBasicBlock to;
    public IRInstJump(IRBasicBlock to) {
        this.to = to;
    }
    @Override
    public String toString() {
        return "br label %" + to.name;
    }
}
