package AST;

import util.position;
import AST.Stmt.StmtNode;

import java.util.ArrayList;

public class DefVarNode extends StmtNode{
    public ArrayList<DefVarUnitNode> variable = new ArrayList<DefVarUnitNode>();
    public DefVarNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
