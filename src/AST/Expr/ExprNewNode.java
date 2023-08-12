package AST.Expr;

import AST.*;
import util.Type;
import util.position;

import java.util.ArrayList;

public class ExprNewNode extends ExprNode{
    public ArrayList<ExprNode> sizeList = new ArrayList<ExprNode>();
    public ExprNewNode(position pos) {
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
