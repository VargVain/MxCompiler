package AST;

import AST.Expr.ExprNode;
import util.Scope;
import util.Type;
import util.position;

public class DefVarUnitNode extends ASTNode{
    public Type type;
    public String name;
    public ExprNode initVal;
    public DefVarUnitNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
