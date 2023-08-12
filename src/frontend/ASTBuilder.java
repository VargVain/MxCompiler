package frontend;

import AST.*;
import AST.Expr.*;
import AST.Stmt.*;
import antlr.*;
import antlr.MxParser.*;
import util.*;
import util.Error;

public class ASTBuilder extends MxParserBaseVisitor<ASTNode> implements Local {
    @Override
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        RootNode node = new RootNode(new position(ctx));
        for (var def : ctx.children) {
            if (def instanceof ClassDefContext) {
                node.Defs.add((DefClassNode) visit(def));
            } else if (def instanceof FuncDefContext) {
                node.Defs.add((DefFuncNode) visit(def));
            } else if (def instanceof VarDefContext) {
                node.Defs.add((DefVarNode) visit(def));
            }
        }
        return node;
    }
    @Override
    public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        DefClassNode node = new DefClassNode(new position(ctx));
        node.name = ctx.Identifier().getText();
        boolean hasConstructor = false;
        for (var def : ctx.children) {
            if (def instanceof FuncDefContext) {
                node.DefFunctions.add((DefFuncNode) visit(def));
            }
            else if (def instanceof VarDefContext) {
                node.DefVariables.add((DefVarNode) visit(def));
            }
            else if (def instanceof ClassBuildContext) {
                if (hasConstructor) {
                    throw new Error(node.pos, "too many constructors");
                }
                node.constructor = (DefConstructorNode) visit(def);
                if (!node.constructor.name.equals(node.name))
                    throw new Error(node.pos, "unmatched constructor");
                hasConstructor = true;
            }
        }
        return node;
    }
    @Override
    public ASTNode visitClassBuild(MxParser.ClassBuildContext ctx) {
        DefConstructorNode node = new DefConstructorNode(new position(ctx));
        node.name = ctx.Identifier().getText();
        node.stmts = ((StmtBlockNode) visit(ctx.suite())).stmts;
        return node;
    }
    @Override
    public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
        DefVarNode node = new DefVarNode(new position(ctx));
        Type type = new Type(ctx.type().typeName().getText(), ctx.type().LeftBracket().size());
        for (var unit : ctx.varDefUnit()) {
            DefVarUnitNode unitNode = new DefVarUnitNode(new position(unit));
            unitNode.name = unit.Identifier().getText();
            unitNode.type = type;
            unitNode.initVal = unit.expr() == null ? null : (ExprNode) visit(unit.expr());
            node.variable.add(unitNode);
        }
        return node;
    }
    @Override
    public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
        DefFuncNode node = new DefFuncNode(new position(ctx));
        node.name = ctx.Identifier().getText();
        node.returnType = ctx.returnType().Void() == null ?
                new Type(ctx.returnType().type().typeName().getText(), ctx.returnType().type().LeftBracket().size()) :
                new Type(ctx.returnType().getText(), 0);
        if (ctx.parameterList() != null) {
            for (int i = 0; i < ctx.parameterList().type().size(); i++) {
                DefVarUnitNode paramNode = new DefVarUnitNode(new position(ctx));
                paramNode.name = ctx.parameterList().Identifier(i).getText();
                paramNode.type = new Type(ctx.parameterList().type(i).typeName().getText(), ctx.parameterList().type(i).LeftBracket().size());
                paramNode.initVal = null;
                node.params.add(paramNode);
            }
        }
        node.stmts = ((StmtBlockNode) visit(ctx.suite())).stmts;
        return node;
    }
    @Override
    public ASTNode visitSuite(MxParser.SuiteContext ctx) {
        StmtBlockNode node = new StmtBlockNode(new position(ctx));
        for (var stmt : ctx.statement()) {
            node.stmts.add((StmtNode) visit(stmt));
        }
        return node;
    }
    @Override
    public ASTNode visitStatement(MxParser.StatementContext ctx) {
        if (ctx.suite() != null)
            return visit(ctx.suite());
        else if (ctx.varDef() != null)
            return visit(ctx.varDef());
        else if (ctx.exprStmt() != null)
            return visit(ctx.exprStmt());
        else if (ctx.ifStmt() != null)
            return visit(ctx.ifStmt());
        else if (ctx.forStmt() != null)
            return visit(ctx.forStmt());
        else if (ctx.whileStmt() != null)
            return visit(ctx.whileStmt());
        else if (ctx.returnStmt() != null)
            return visit(ctx.returnStmt());
        else if (ctx.breakStmt() != null)
            return visit(ctx.breakStmt());
        else if (ctx.continueStmt() != null)
            return visit(ctx.continueStmt());
        else
            return visitChildren(ctx);
    }
    @Override
    public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
        StmtIfNode node = new StmtIfNode(new position(ctx));
        node.condition = (ExprNode) visit(ctx.expr());
        if (ctx.statement(0).suite() != null) {
            node.thenStmts = ((StmtBlockNode) visit(ctx.statement(0).suite())).stmts;
        }
        else {
            node.thenStmts.add((StmtNode) visit(ctx.statement(0)));
        }
        if (ctx.Else() != null) {
            if (ctx.statement(1).suite() != null) {
                node.elseStmts = ((StmtBlockNode) visit(ctx.statement(1).suite())).stmts;
            }
            else {
                node.elseStmts.add((StmtNode) visit(ctx.statement(1)));
            }
        }
        return node;
    }
    @Override
    public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        StmtReturnNode node = new StmtReturnNode(new position(ctx));
        node.expr = ctx.expr() == null ? null : (ExprNode) visit(ctx.expr());
        return node;
    }
    @Override
    public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        return new StmtBreakNode(new position(ctx));
    }
    @Override
    public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        return new StmtContinueNode(new position(ctx));
    }
    @Override
    public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
        StmtForNode node = new StmtForNode(new position(ctx));
        if (ctx.forInit().varDef() != null) {
            node.InitDef = (DefVarNode) visit(ctx.forInit().varDef());
        }
        else {
            node.InitVar = ((StmtExprNode) visit(ctx.forInit().exprStmt())).expr;
        }
        node.cond = (ExprNode) visit(ctx.exprStmt().expr());
        node.step = (ExprNode) visit(ctx.expr());
        if (ctx.statement().suite() != null) {
            node.stmts = ((StmtBlockNode) visit(ctx.statement())).stmts;
        }
        else {
            node.stmts.add((StmtNode) visit(ctx.statement()));
        }
        return node;
    }
    @Override
    public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
        StmtWhileNode node = new StmtWhileNode(new position(ctx));
        node.cond = (ExprNode) visit(ctx.expr());
        if (ctx.statement().suite() != null) {
            node.stmts = ((StmtBlockNode) visit(ctx.statement())).stmts;
        }
        else {
            node.stmts.add((StmtNode) visit(ctx.statement()));
        }
        return node;
    }
    @Override
    public ASTNode visitExprStmt(MxParser.ExprStmtContext ctx) {
        StmtExprNode node = new StmtExprNode(new position(ctx));
        node.expr = ctx.expr() == null ? null : (ExprNode) visit(ctx.expr());
        return node;
    }
    @Override
    public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        var node = new ExprNewNode(new position(ctx));
        node.type = new Type(ctx.typeName().getText(), ctx.newArrayUnit().size());
        boolean lastEmpty = false;
        for (var unit : ctx.newArrayUnit()) {
            if (unit.expr() == null)
                lastEmpty = true;
            else if (lastEmpty)
                throw new Error(new position(ctx), "invalid empty dimensions in an array");
            else
                node.sizeList.add((ExprNode) visit(unit.expr()));
        }
        return node;
    }
    @Override
    public ASTNode visitUnaryExpr(MxParser.UnaryExprContext ctx) {
        return new ExprUnaryNode(new position(ctx), ctx.op.getText(), (ExprNode) visit(ctx.expr()));
    }
    @Override
    public ASTNode visitPreAddExpr(MxParser.PreAddExprContext ctx) {
        return new ExprPreAddNode(new position(ctx), ctx.op.getText(), (ExprNode) visit(ctx.expr()));
    }
    @Override
    public ASTNode visitFuncExpr(MxParser.FuncExprContext ctx) {
        var node = new ExprFuncNode(new position(ctx));
        node.funcName = (ExprNode) visit(ctx.expr());
        if (ctx.exprList() != null)
            node.args = (ExprListNode) visit(ctx.exprList());
        return node;
    }
    @Override
    public ASTNode visitArrayExpr(MxParser.ArrayExprContext ctx) {
        var node = new ExprArrayNode(new position(ctx), (ExprNode) visit(ctx.expr(0)), (ExprNode) visit(ctx.expr(1)));
        node.str = ctx.getText();
        return node;
    }
    @Override
    public ASTNode visitMemberExpr(MxParser.MemberExprContext ctx) {
        var node = new ExprMemberNode(new position(ctx));
        node.obj = (ExprNode) visit(ctx.expr());
        node.member = ctx.Identifier().getText();
        node.str = ctx.getText();
        return node;
    }
    @Override
    public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
        var node = (ExprNode) visitChildren(ctx);
        node.str = ctx.getText();
        return node;
    }
    @Override
    public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        return new ExprBinaryNode(
                new position(ctx),
                (ExprNode) visit(ctx.expr(0)),
                ctx.op.getText(),
                (ExprNode) visit(ctx.expr(1)));
    }
    @Override
    public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
        return new ExprAssignNode(new position(ctx),
                (ExprNode) visit(ctx.expr(0)),
                (ExprNode) visit(ctx.expr(1)));
    }
    @Override
    public ASTNode visitParenExpr(MxParser.ParenExprContext ctx) {
        return (ExprNode) visit(ctx.expr());
    }
    @Override
    public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
        if (ctx.Identifier() == null) {
            ExprBasicNode node = new ExprBasicNode(new position(ctx));
            if (ctx.IntegerLiteral() != null) node.type = IntType;
            else if (ctx.StringLiteral() != null) node.type = StringType;
            else if (ctx.True() != null || ctx.False() != null) node.type = BoolType;
            else if (ctx.Null() != null) node.type = NullType;
            else if (ctx.This() != null) node.type = ThisType;
            return node;
        }
        else return new ExprValNode(new position(ctx), ctx.getText());
    }
    @Override
    public ASTNode visitExprList(MxParser.ExprListContext ctx) {
        ExprListNode node = new ExprListNode(new position(ctx));
        ctx.expr().forEach(expr -> node.exprs.add((ExprNode) visit(expr)));
        return node;
    }
    @Override
    public ASTNode visitCondExpr(MxParser.CondExprContext ctx) {
        ExprCondNode node = new ExprCondNode(new position(ctx));
        node.cond = (ExprNode) visit(ctx.expr(0));
        node.expr1 = (ExprNode) visit(ctx.expr(1));
        node.expr2 = (ExprNode) visit(ctx.expr(2));
        return node;
    }
}