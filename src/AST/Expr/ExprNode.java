package AST.Expr;

import AST.ASTNode;
import AST.DefFuncNode;
import util.*;

public abstract class ExprNode extends ASTNode {
    public String str = null;
    public DefFuncNode function = null;
    public Type type;
    public ExprNode(position pos) {
        super(pos);
    }
    public abstract boolean isLeftValue();
}
