package AST.Expr;

import AST.*;
import util.position;

public class ExprBasicNode extends ExprNode{
    public ExprBasicNode(position pos) {
        super(pos);
    }
    @Override
    public boolean isLeftValue() {
        return false;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
