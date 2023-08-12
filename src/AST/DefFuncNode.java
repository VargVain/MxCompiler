package AST;

import AST.Stmt.StmtNode;
import util.Type;
import util.position;

import java.util.ArrayList;

public class DefFuncNode extends ASTNode{
    public Type returnType;
    public String name;
    public ArrayList<DefVarUnitNode> params = new ArrayList<DefVarUnitNode>();
    public ArrayList<StmtNode> stmts = new ArrayList<StmtNode>();
    public DefFuncNode(position pos) {
        super(pos);
    }
    public DefFuncNode(position pos, String name, String returnTypeName, String paramTypeName, int paramCnt) {
        super(pos);
        this.name = name;
        this.returnType = new Type(returnTypeName, 0);
        if (paramTypeName != null) {
            Type paramType = new Type(paramTypeName, 0);
            for (int i = 0; i < paramCnt; i++) {
                DefVarUnitNode unit = new DefVarUnitNode(pos);
                unit.type = paramType;
                this.params.add(unit);
            }
        }
    }
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
