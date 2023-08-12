package AST.Expr;

import AST.*;
import util.position;

public class ExprMemberNode extends ExprNode{
    public ExprNode obj;
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
