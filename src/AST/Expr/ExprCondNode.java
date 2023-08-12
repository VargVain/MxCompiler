package AST.Expr;

import AST.*;
import util.position;

public class ExprCondNode extends ExprNode{
    public ExprNode cond, expr1, expr2;
    public ExprCondNode(position pos) {
        super(pos);
    }
    @Override
    public boolean isLeftValue() {
        return expr1.isLeftValue();
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
