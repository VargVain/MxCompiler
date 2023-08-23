package AST.Expr;

import AST.ASTNode;
import AST.DefFuncNode;
import util.*;
import IR.val.IRVal;

public abstract class ExprNode extends ASTNode {
    public String str = null;
    public IRVal irVal = null, irPtr = null;
    public DefFuncNode function = null;
    public Type type;
    public ExprNode(position pos) {
        super(pos);
    }
    public abstract boolean isLeftValue();
}
