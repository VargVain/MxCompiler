package AST.Expr;

import AST.*;
import util.position;

public class ExprBinaryNode extends ExprNode{
    public String op;
    public ExprNode lhs, rhs;
    public ExprBinaryNode(position pos, ExprNode lhs, String op, ExprNode rhs) {
        super(pos);
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
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
