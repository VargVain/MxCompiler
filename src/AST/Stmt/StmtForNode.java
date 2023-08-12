package AST.Stmt;

import AST.ASTVisitor;
import AST.DefVarNode;
import AST.Expr.ExprNode;
import util.position;

import java.util.ArrayList;


public class StmtForNode extends StmtNode{
    public DefVarNode InitDef;
    public ExprNode InitVar, cond, step;
    public ArrayList<StmtNode> stmts = new ArrayList<StmtNode>();
    public StmtForNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
