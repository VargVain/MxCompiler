package IR.inst;

import IR.val.IRVal;
import IR.IRBasicBlock;

public class IRInstBranch extends IRInst{
    public IRVal cond;
    public IRBasicBlock thenBlock, elseBlock;
    public IRInstBranch(IRVal cond, IRBasicBlock thenBlock, IRBasicBlock elseBlock) {
        this.cond = cond;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }
    @Override
    public String toString() {
        return "br " + cond + ", label %" + thenBlock.name + ", label %" + elseBlock.name;
    }
}
