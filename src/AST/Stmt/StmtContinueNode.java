package AST.Stmt;

import AST.ASTVisitor;
import util.position;

public class StmtContinueNode extends StmtNode{
    public StmtContinueNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
