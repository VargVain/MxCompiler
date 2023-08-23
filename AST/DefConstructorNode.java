package AST;

import AST.Stmt.StmtNode;
import util.position;

import java.util.ArrayList;


public class DefConstructorNode extends ASTNode{
    public String name;
    public String classname;
    public ArrayList<StmtNode> stmts = new ArrayList<StmtNode>();
    public DefConstructorNode(position pos) {
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
