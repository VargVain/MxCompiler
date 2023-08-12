package AST;

import AST.Stmt.*;
import AST.Expr.*;

public interface ASTVisitor {
    public void visit(RootNode node);
    public void visit(DefVarNode node);
    public void visit(DefVarUnitNode node);
    public void visit(DefClassNode node);
    public void visit(DefConstructorNode node);
    public void visit(DefFuncNode node);

    public void visit(StmtBlockNode node);
    public void visit(StmtExprNode node);
    public void visit(StmtIfNode node);
    public void visit(StmtReturnNode node);
    public void visit(StmtBreakNode node);
    public void visit(StmtContinueNode node);
    public void visit(StmtForNode node);
    public void visit(StmtWhileNode node);

    public void visit(ExprArrayNode node);
    public void visit(ExprAssignNode node);
    public void visit(ExprBasicNode node);
    public void visit(ExprBinaryNode node);
    public void visit(ExprCondNode node);
    public void visit(ExprFuncNode node);
    public void visit(ExprListNode node);
    public void visit(ExprMemberNode node);
    public void visit(ExprNewNode node);
    public void visit(ExprPreAddNode node);
    public void visit(ExprUnaryNode node);
    public void visit(ExprValNode node);
}
