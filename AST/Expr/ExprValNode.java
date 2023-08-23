package AST.Expr;

import AST.*;
import util.position;

public class ExprValNode extends ExprNode{
    public ExprValNode(position pos, String str) {
        super(pos);
        this.str = str;
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

