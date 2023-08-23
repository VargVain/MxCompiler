package AST;

import util.ClassMembers;
import util.position;

import java.util.ArrayList;

public class DefClassNode extends ASTNode{
    public String name;
    public ArrayList<DefVarNode> DefVariables = new ArrayList<DefVarNode>();
    public ArrayList<DefFuncNode> DefFunctions = new ArrayList<DefFuncNode>();
    public DefConstructorNode constructor;
    public ClassMembers classMembers = new ClassMembers();
    public DefClassNode(position pos) {
        super(pos);
    }
    public DefClassNode(position pos, String name) {
        super(pos);
        this.name = name;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
