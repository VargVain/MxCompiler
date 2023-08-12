package frontend;

import AST.*;
import AST.Expr.*;
import AST.Stmt.*;
import util.*;
import util.Error;

public class SemanticChecker implements ASTVisitor, Local{
    public GlobalScope globalScope;
    public Scope currentScope;
    public SemanticChecker(GlobalScope globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
    }
    @Override
    public void visit(RootNode node) {
        if (!globalScope.hasFunc("main")) {
            throw new Error(node.pos, "missing main function");
        }
        if (!globalScope.getFunc("main").returnType.equals(IntType)) {
            throw new Error(node.pos, "incorrect return type of main function");
        }
        for (var def : node.Defs) {
            def.accept(this);
        }
    }
    @Override
    public void visit(DefVarNode node) {
        node.variable.forEach(var -> var.accept(this));
    }
    @Override
    public void visit(DefVarUnitNode node) {
        if (!node.type.checkIfTypeDefined(globalScope))
            throw new Error(node.pos, "undefined type " + node.type.toString());
        if (node.initVal != null) {
            node.initVal.accept(this);
            if (!node.initVal.type.equals(node.type) && !node.initVal.type.equals(NullType))
                throw new Error(node.pos, "unmatched type, required: " + node.type.toString()
                        + ", provided: " + node.initVal.type.toString());
        }
        if (currentScope.hasVar(node.name))
            throw new Error(node.pos, "redefinition of variable " + node.name);
        currentScope.putVar(node.name, node);
    }
    @Override
    public void visit(DefClassNode node) {
        currentScope = new Scope(currentScope);
        currentScope.parentClass = node;
        node.DefVariables.forEach(vars -> vars.accept(this));
        node.DefFunctions.forEach(func -> func.accept(this));
        if (node.constructor != null)
            node.constructor.accept(this);
        currentScope = currentScope.parentScope;
    }
    @Override
    public void visit(DefConstructorNode node) {
        currentScope = new Scope(currentScope);
        currentScope.returnType = VoidType;
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
    }
    @Override
    public void visit(DefFuncNode node) {
        if (!node.returnType.checkIfTypeDefined(globalScope))
            throw new Error(node.pos, "undefined type " + node.returnType.toString());
        currentScope = new Scope(currentScope);
        currentScope.returnType = node.returnType;
        node.params.forEach(param -> param.accept(this));
        node.stmts.forEach(stmt -> stmt.accept(this));
        if (!currentScope.returned && !node.returnType.equals(VoidType) && !node.name.equals("main"))
            throw new Error(node.pos, "missing return statement");
        currentScope = currentScope.parentScope;
    }
    @Override
    public void visit(StmtBlockNode node) {
        currentScope = new Scope(currentScope);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
    }
    @Override
    public void visit(StmtExprNode node) {
        if (node.expr != null) node.expr.accept(this);
    }
    @Override
    public void visit(StmtIfNode node) {
        node.condition.accept(this);
        if (!node.condition.type.equals(BoolType))
            throw new Error(node.pos, "invalid condition expression");
        currentScope = new Scope(currentScope);
        node.thenStmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
        if (node.elseStmts != null) {
            currentScope = new Scope(currentScope);
            node.elseStmts.forEach(stmt -> stmt.accept(this));
            currentScope = currentScope.parentScope;
        }
    }
    @Override
    public void visit(StmtReturnNode node) {
        for (var nowScope = currentScope; nowScope != null; nowScope = nowScope.parentScope) {
            if (nowScope.returnType != null) {
                if (node.expr == null) {
                    if (!nowScope.returnType.equals(VoidType)) {
                        throw new Error(node.pos, "unmatched return type");
                    }
                }
                else {
                    node.expr.accept(this);
                    if (!nowScope.returnType.equals(node.expr.type)
                            && !(node.expr.type.equals(NullType) && nowScope.returnType.isRef())) {
                        throw new Error(node.pos, "unmatched return type");
                    }
                }
                nowScope.returned = true;
                return;
            }
        }
        throw new Error(node.pos, "invalid return statement");
    }
    @Override
    public void visit(StmtBreakNode node) {
        if (!currentScope.looped)
            throw new Error(node.pos, "invalid break statement");
    }
    @Override
    public void visit(StmtContinueNode node) {
        if (!currentScope.looped)
            throw new Error(node.pos, "invalid continue statement");
    }
    @Override
    public void visit(StmtForNode node) {
        currentScope = new Scope(currentScope);
        currentScope.looped = true;
        if (node.InitVar != null)
            node.InitVar.accept(this);
        if (node.InitDef != null)
            node.InitDef.accept(this);
        if (node.cond != null) {
            node.cond.accept(this);
            if (!(node.cond.type.equals(BoolType)))
                throw new Error(node.pos, "invalid condition expression");
        }
        if (node.step != null)
            node.step.accept(this);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
    }
    @Override
    public void visit(StmtWhileNode node) {
        node.cond.accept(this);
        if (!(node.cond.type.equals(BoolType)))
            throw new Error(node.pos, "invalid condition expression");
        currentScope = new Scope(currentScope);
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentScope = currentScope.parentScope;
    }
    @Override
    public void visit(ExprArrayNode node) {
        node.index.accept(this);
        if (!node.index.type.equals(IntType))
            throw new Error(node.pos, "invalid index expression");
        node.array.accept(this);
        if (node.array.type.equals(NullType) || node.array.type.dim < 1)
            throw new Error(node.pos, "invalid array expression");

        node.type = new Type(node.array.type.typeName, node.array.type.dim - 1);
    }
    @Override
    public void visit(ExprAssignNode node) {
        node.lhs.accept(this);
        if (!node.lhs.isLeftValue())
            throw new Error(node.pos, "invalid left value");
        node.rhs.accept(this);
        if (node.lhs.type == null || node.lhs.type.equals(VoidType) || node.rhs.type == null || node.rhs.type.equals(VoidType))
            throw new Error(node.pos, "invalid expression");
        if (!node.lhs.type.equals(node.rhs.type) && !(node.lhs.type.isRef() && node.rhs.type.equals(NullType)))
            throw new Error(node.pos, "unmatched type");
        node.type = node.lhs.type;
    }
    @Override
    public void visit(ExprBasicNode node) {
        if (node.type.equals(ThisType)) {
            if (currentScope.parentClass == null)
                throw new Error(node.pos, "invalid this expression");
            node.type = new Type(currentScope.parentClass.name, 0);
        }
    }
    @Override
    public void visit(ExprBinaryNode node) {
        node.lhs.accept(this);
        node.rhs.accept(this);
        if (node.lhs.type == null || node.lhs.type.equals(VoidType) || node.rhs.type == null || node.rhs.type.equals(VoidType))
            throw new Error(node.pos, "invalid expression");
        if (!node.lhs.type.equals(node.rhs.type)) {
            if (node.lhs.type.isRefOrNull() && node.rhs.type.isRefOrNull() && (node.op.equals("==") || node.op.equals("!="))) {
                node.type = BoolType;
                return;
            }
            else throw new Error(node.pos, "unmatched type");
        }
        switch (node.op) {
            case "+", "<=", ">=", "<", ">" -> {
                if (!(node.lhs.type.equals(IntType)) && !(node.lhs.type.equals(StringType)))
                    throw new Error(node.pos, "unmatched type");
                node.type = node.op.equals("+") ? node.lhs.type : BoolType;
            }
            case "-", "*", "/", "%", "<<", ">>", "&", "|", "^" -> {
                if (!(node.lhs.type.equals(IntType)))
                    throw new Error(node.pos, "unmatched type");
                node.type = IntType;
            }
            case "&&", "||" -> {
                if (!(node.lhs.type.equals(BoolType)))
                    throw new Error(node.pos, "unmatched type");
                node.type = BoolType;
            }
            case "==", "!=" -> node.type = BoolType;
        }
    }
    @Override
    public void visit(ExprCondNode node) {
        node.cond.accept(this);
        if (!node.cond.type.equals(BoolType))
            throw new Error(node.pos, "invalid condition");
        node.expr1.accept(this);
        node.expr2.accept(this);
        if (!node.expr1.type.equals(node.expr2.type))
            throw new Error(node.pos, "unmatched type");
        node.type = node.expr1.type;
    }
    @Override
    public void visit(ExprFuncNode node) {
        node.funcName.accept(this);
        if (node.funcName.function == null)
            throw new Error(node.pos, "function " + node.funcName.str + " is not defined");
        var funcDef = node.funcName.function;
        if (node.args != null) {
            node.args.accept(this);
            if (funcDef.params == null || funcDef.params.size() != node.args.exprs.size())
                throw new Error(node.pos, "unmatched parameter");
            for (int i = 0; i < funcDef.params.size(); i++) {
                var param = funcDef.params.get(i);
                var arg = node.args.exprs.get(i);
                if (!param.type.equals(arg.type) && !(param.type.isRef() && arg.type.equals(NullType)))
                    throw new Error(node.pos, "unmatched parameter");
            }
        } else {
            if (!funcDef.params.isEmpty())
                throw new Error(node.pos, "unmatched parameter");
        }
        node.type = funcDef.returnType;
    }
    @Override
    public void visit(ExprListNode node) {
        node.exprs.forEach(expr -> expr.accept(this));
    }
    @Override
    public void visit(ExprMemberNode node) {
        node.obj.accept(this);
        if (node.obj.type == null)
            throw new Error(node.pos, "invalid object");
        if (!node.obj.type.isRef() && !node.obj.type.equals(ThisType) && node.obj.type.equals(StringType))
            throw new Error(node.pos, "unmatched type");
        var classDef = node.obj.type.equals(ThisType)
                ? currentScope.parentClass
                : globalScope.getClass(node.obj.type.typeName);
        if (classDef == null)
            throw new Error(node.pos, "unmatched type");
        if (node.obj.type.dim > 0) {
            if (node.member.equals("size")) {
                node.function = ArraySizeFunc;
            }
            else {
                throw new Error(node.pos, "invalid function");
            }
        } else {
            node.type = classDef.classMembers.hasVar(node.member) ?
                        classDef.classMembers.getVar(node.member).type :
                        null;
            node.function = classDef.classMembers.getFunc(node.member);
        }
    }
    @Override
    public void visit(ExprNewNode node) {
        for (var size : node.sizeList) {
            size.accept(this);
            if (size.type == null || !size.type.equals(IntType))
                throw new Error(node.pos, "invalid expression");
        }
        if (!node.type.checkIfTypeDefined(globalScope))
            throw new Error(node.pos, "undefined type " + node.type.toString());
    }
    @Override
    public void visit(ExprPreAddNode node) {
        node.expr.accept(this);
        if (node.expr.type == null || !node.expr.type.equals(IntType))
            throw new Error(node.pos, "invalid expression");
        if (!node.expr.isLeftValue())
            throw new Error(node.pos, "invalid left value");
        node.type = IntType;
    }
    @Override
    public void visit(ExprUnaryNode node) {
        node.expr.accept(this);
        if (node.expr.type == null)
            throw new Error(node.pos, "invalid expression");
        switch (node.op) {
            case "++", "--" -> {
                if (!node.expr.isLeftValue() || !node.expr.type.equals(IntType))
                    throw new Error(node.pos, "invalid left value");
                node.type = IntType;
            }
            case "!" -> {
                if (!node.expr.type.equals(BoolType))
                    throw new Error(node.pos, "invalid type");
                node.type = BoolType;
            }
            default -> {
                if (!node.expr.type.equals(IntType))
                    throw new Error(node.pos, "invalid type");
                node.type = IntType;
            }
        }
    }
    @Override
    public void visit(ExprValNode node) {
        node.type = currentScope.searchVar(node.str);
        if (currentScope.parentClass != null && currentScope.parentClass.classMembers.hasFunc(node.str))
            node.function = currentScope.parentClass.classMembers.getFunc(node.str);
        else
            node.function = globalScope.getFunc(node.str);
    }
}
