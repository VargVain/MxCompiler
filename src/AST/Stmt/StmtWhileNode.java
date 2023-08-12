package AST.Stmt;

import AST.ASTVisitor;
import AST.Expr.ExprNode;
import util.position;

import java.util.ArrayList;

public class StmtWhileNode extends StmtNode{
    public ExprNode cond;
    public ArrayList<StmtNode> stmts = new ArrayList<StmtNode>();
    public StmtWhileNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
