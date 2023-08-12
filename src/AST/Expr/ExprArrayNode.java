package AST.Expr;

import AST.*;
import util.position;

public class ExprArrayNode extends ExprNode{
    public ExprNode array;
    public ExprNode index;
    public ExprArrayNode(position pos, ExprNode array, ExprNode index) {
        super(pos);
        this.array = array;
        this.index = index;
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
