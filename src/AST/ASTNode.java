package AST;

import util.Local;
import util.position;

abstract public class ASTNode implements Local {
    public position pos;

    public ASTNode(position pos) {
        this.pos = pos;
    }

    abstract public void accept(ASTVisitor visitor);
}