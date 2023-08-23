package AST.Stmt;

import AST.ASTVisitor;
import util.position;

public class StmtBreakNode extends StmtNode{
    public StmtBreakNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
