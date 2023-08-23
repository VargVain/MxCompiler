package AST.Stmt;

import AST.ASTVisitor;
import AST.Expr.ExprNode;
import util.position;

import java.util.ArrayList;

public class StmtIfNode extends StmtNode{
    public ExprNode condition;
    public ArrayList<StmtNode> thenStmts = new ArrayList<StmtNode>();
    public ArrayList<StmtNode> elseStmts = new ArrayList<StmtNode>();
    public StmtIfNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
