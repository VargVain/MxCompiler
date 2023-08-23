package AST.Expr;

import AST.*;
import IR.val.IRTemp;
import util.position;

public class ExprMemberNode extends ExprNode{
    public ExprNode obj;
    public IRTemp objAddr;
    public String member;
    public ExprMemberNode(position pos) {
        super(pos);
    }
    @Override
    public boolean isLeftValue() {
        return true;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
