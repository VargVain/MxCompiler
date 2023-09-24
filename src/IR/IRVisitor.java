package IR;

import IR.inst.*;

public interface IRVisitor {
    public void visit(IRRoot node);
    public void visit(IRFunction node);
    public void visit(IRBasicBlock node);
    public void visit(IRInstAlloca node);
    public void visit(IRInstBinary node);
    public void visit(IRInstBranch node);
    public void visit(IRInstBitcast node);
    public void visit(IRInstCall node);
    public void visit(IRInstGEP node);
    public void visit(IRInstIcmp node);
    public void visit(IRInstJump node);
    public void visit(IRInstLoad node);
    public void visit(IRInstRet node);
    public void visit(IRInstStore node);
    public void visit(IRInstPhi node);
}
