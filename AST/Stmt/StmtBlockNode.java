package AST.Stmt;

import AST.ASTVisitor;
import util.position;

import java.util.ArrayList;

public class StmtBlockNode extends StmtNode{
    public ArrayList<StmtNode> stmts = new ArrayList<StmtNode>();
    public StmtBlockNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
