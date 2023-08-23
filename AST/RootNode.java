package AST;

import util.Scope;
import util.position;
import java.util.ArrayList;

public class RootNode extends ASTNode{
    public ArrayList<ASTNode> Defs = new ArrayList<ASTNode>();
    public RootNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
