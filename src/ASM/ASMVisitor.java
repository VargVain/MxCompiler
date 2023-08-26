package ASM;

import ASM.inst.*;

public interface ASMVisitor {
    void visit(ASMRoot node);
    public void visit(ASMFunc node);
    public void visit(ASMBlock node);
    public void visit(ASMInstAddi node);
    public void visit(ASMInstBinary node);
    public void visit(ASMInstBranch node);
    public void visit(ASMInstCall node);
    public void visit(ASMInstJump node);
    public void visit(ASMInstLi node);
    public void visit(ASMInstLoad node);
    public void visit(ASMInstLui node);
    public void visit(ASMInstMove node);
    public void visit(ASMInstRet node);
    public void visit(ASMInstStore node);
    public void visit(ASMInstUnary node);
}
