package AST.Expr;

import AST.*;
import util.position;

public class ExprFuncNode extends ExprNode{
    public ExprNode funcName;
    public ExprListNode args;
    public ExprFuncNode(position pos) {
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
