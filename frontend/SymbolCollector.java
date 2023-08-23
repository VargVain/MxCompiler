package frontend;

import AST.*;
import AST.Expr.*;
import AST.Stmt.*;
import util.*;
import util.Error;

public class SymbolCollector implements ASTVisitor{
    public GlobalScope rootScope;
    public SymbolCollector(GlobalScope rootScope) {
        this.rootScope = rootScope;
    }
    @Override
    public void visit(RootNode node) {
        for (var def : node.Defs) {
            def.accept(this);
        }
    }
    @Override
    public void visit(DefVarNode node) {

    }
    @Override
    public void visit(DefVarUnitNode node) {

    }
    @Override
    public void visit(DefClassNode node) {
        if (rootScope.hasClass(node.name)) {
            throw new Error(node.pos, "class " + node.name + " is already defined");
        }
        if (rootScope.hasFunc(node.name)) {
            throw new Error(node.pos, "class " + node.name + " is already defined");
        }
        rootScope.putClass(node.name, node);
        for (var func : node.DefFunctions) {
            if (node.classMembers.hasFunc(func.name))
                throw new Error(func.pos, "function " + func.name + " is already defined");
            node.classMembers.putFunc(func.name, func);
        }
        for (var vars : node.DefVariables) {
            for (var var : vars.variable) {
                if (node.classMembers.hasVar(var.name))
                    throw new Error(var.pos, "variable " + var.name + " is already defined");
                node.classMembers.putVar(var.name, var);
            }
        }
    }

    @Override
    public void visit(DefConstructorNode node) {

    }

    @Override
    public void visit(DefFuncNode node) {
        if (rootScope.hasFunc(node.name)) {
            throw new Error(node.pos, "function " + node.name + " is already defined");
        }
        if (rootScope.hasClass(node.name)) {
            throw new Error(node.pos, "function " + node.name + " is already defined");
        }
        rootScope.putFunc(node.name, node);
    }
    @Override
    public void visit(StmtBlockNode node) {

    }
    @Override
    public void visit(StmtExprNode node) {

    }
    @Override
    public void visit(StmtIfNode node) {

    }
    @Override
    public void visit(StmtReturnNode node) {

    }
    @Override
    public void visit(StmtBreakNode node) {

    }
    @Override
    public void visit(StmtContinueNode node) {

    }
    @Override
    public void visit(StmtForNode node) {

    }
    @Override
    public void visit(StmtWhileNode node) {

    }
    @Override
    public void visit(ExprArrayNode node) {

    }
    @Override
    public void visit(ExprAssignNode node) {

    }
    @Override
    public void visit(ExprBasicNode node) {

    }
    @Override
    public void visit(ExprBinaryNode node) {

    }
    @Override
    public void visit(ExprCondNode node) {

    }
    @Override
    public void visit(ExprFuncNode node) {

    }
    @Override
    public void visit(ExprListNode node) {

    }
    @Override
    public void visit(ExprMemberNode node) {

    }
    @Override
    public void visit(ExprNewNode node) {

    }
    @Override
    public void visit(ExprPreAddNode node) {

    }
    @Override
    public void visit(ExprUnaryNode node) {

    }
    @Override
    public void visit(ExprValNode node) {

    }
}
