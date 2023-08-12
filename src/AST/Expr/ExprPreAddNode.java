package AST.Expr;

import AST.*;
import util.position;

public class ExprPreAddNode extends ExprUnaryNode{
    public ExprPreAddNode(position pos, String op, ExprNode expr) {
        super(pos, op, expr);
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
