package AST.Expr;

import AST.*;
import util.position;

public class ExprUnaryNode extends ExprNode{
    public String op;
    public ExprNode expr;
    public ExprUnaryNode(position pos, String op, ExprNode expr) {
        super(pos);
        this.op = op;
        this.expr = expr;
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