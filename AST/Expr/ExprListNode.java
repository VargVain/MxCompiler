package AST.Expr;

import AST.*;
import util.position;

import java.util.ArrayList;

public class ExprListNode extends ASTNode{
    public ArrayList<ExprNode> exprs = new ArrayList<ExprNode>();
    public ExprListNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
