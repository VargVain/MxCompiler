package AST.Stmt;

import AST.ASTVisitor;
import AST.Expr.ExprNode;
import util.position;

public class StmtReturnNode extends StmtNode{
    public ExprNode expr;
    public StmtReturnNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {

    }
}
