package AST.Expr;

import AST.*;
import util.position;

public class ExprAssignNode extends ExprBinaryNode{
    public ExprAssignNode(position pos, ExprNode lhs, ExprNode rhs) {
        super(pos, lhs, "=", rhs);
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
