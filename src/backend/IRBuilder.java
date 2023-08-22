package backend;

import AST.*;
import AST.Expr.*;
import AST.Stmt.*;
import IR.*;
import IR.inst.*;
import IR.type.*;
import IR.val.*;
import util.*;
import util.Error;

import java.util.ArrayList;
import java.util.HashMap;

public class IRBuilder implements ASTVisitor, Local{
    public IRFunction currentFunction = null;
    public IRBasicBlock currentBlock = null;
    public IRTypeStruct currentClass = null;
    public GlobalScope globalScope;
    public Scope currentScope;
    public HashMap<String, IRTypeStruct> structTypeMap = new HashMap<>();
    public IRRoot root;
    public IRBuilder(IRRoot root, GlobalScope globalScope) {
        this.root = root;
        this.globalScope = globalScope;
        currentScope = globalScope;
    }
    @Override
    public void visit(RootNode node) {
        for (var def : node.Defs) {
            if (def instanceof DefClassNode) {
                structTypeMap.put(((DefClassNode) def).name, new IRTypeStruct(((DefClassNode) def).name, ((DefClassNode) def).classMembers.variables.size() << 2));
            }
        }
        for (var def : node.Defs) {
            if (def instanceof DefClassNode) def.accept(this);
        }
        for (var def : node.Defs) {
            if (def instanceof DefVarNode) def.accept(this);
        }
        root.functions.add(root.globalValInit);
        for (var def : node.Defs) {
            if (def instanceof DefFuncNode) def.accept(this);
        }
        IRBasicBlock block = root.globalValInit.blocks.get(0);
        block.addInst(new IRInstRet("Void"));
    }
    @Override
    public void visit(DefVarNode node) {
        node.variable.forEach(var -> var.accept(this));
    }
    @Override
    public void visit(DefVarUnitNode node) {
        IRType type = node.type.typeTrans(structTypeMap);
        if (currentFunction != null) {
            String name = currentScope.register(node.name, currentFunction.register(node.name));
            IRVariable var = new IRVariable(new IRTypePtr(type), name);
            currentBlock.addInst(new IRInstAlloca(var, type));
            currentScope.putIRVal(node.name, var);
            if (node.initVal != null) {
                node.initVal.accept(this);
                currentBlock.addInst(new IRInstStore(node.initVal.irVal, var));
            }
        } else if (currentClass != null) {
            currentClass.addMember(node.name, node.type.typeTrans(structTypeMap));
        } else {
            String name = "@" + node.name;
            IRVariable var = new IRVariable(new IRTypePtr(type), name);
            currentScope.putIRVal(node.name, var);
            root.variables.add(var);
            if (node.initVal != null) {
                if (node.initVal instanceof ExprBasicNode && !node.initVal.type.equals(StringType)) {
                    node.initVal.accept(this);
                    var.initVal = node.initVal.irVal;
                }
                else {
                    var.initVal = defaultVal(type);
                    currentBlock = root.globalValInit.blocks.get(0);
                    node.initVal.accept(this);
                    currentBlock.addInst(new IRInstStore(getVal(node.initVal), var));
                    currentBlock = null;
                }
            } else var.initVal = defaultVal(type);
        }
    }
    @Override
    public void visit(DefClassNode node) {
        currentScope = new Scope(currentScope);
        currentClass = structTypeMap.get(node.name);
        root.classes.add(currentClass);
        node.DefVariables.forEach(varDef -> varDef.accept(this));
        if (node.constructor != null) {
            currentClass.hasConstructor = true;
            node.constructor.accept(this);
        }
        node.DefFunctions.forEach(funcDef -> funcDef.className = node.name);
        node.DefFunctions.forEach(funcDef -> funcDef.accept(this));
        currentScope = currentScope.parentScope;
        currentClass = null;
    }
    @Override
    public void visit(DefConstructorNode node) {
        DefFuncNode constructor = new DefFuncNode(node);
        constructor.accept(this);
    }
    @Override
    public void visit(DefFuncNode node) {
        IRBasicBlock.blockCnt = 0;
        IRTemp.TempValCnt = 0;
        IRFunction function = new IRFunction();
        function.name = node.name;
        function.returnType = node.returnType.typeTrans(structTypeMap);
        currentFunction = function;
        currentBlock = function.newBlock("entry");
        currentScope = new Scope(currentScope);

        if (node.name.equals("main")) {
            currentBlock.addInst(new IRInstCall(".globalVal.init"));
        }

        if (currentClass != null) {
            IRTypePtr classPtrType = new IRTypePtr(currentClass);
            IRTemp thisVal = new IRTemp("this", classPtrType);
            currentFunction.params.add(thisVal);
            IRVariable thisAddr = new IRVariable(new IRTypePtr(classPtrType), "this");
            currentBlock.addInst(new IRInstAlloca(thisAddr, classPtrType));
            currentBlock.addInst(new IRInstStore(thisVal, thisAddr));
            currentScope.putIRVal("this", thisAddr);
        }

        for (var param : node.params) {
            IRTemp temp = new IRTemp(param.type.typeTrans(structTypeMap));
            currentFunction.params.add(temp);
            param.accept(this);
            currentBlock.addInst(new IRInstStore(temp, currentScope.getIRVal(param.name)));
        }

        node.stmts.forEach(stmt -> stmt.accept(this));
        if (node.returnType.equals(VoidType)) {
            for (var block : function.blocks) {
                if (!block.instructions.isEmpty()) {
                    IRInst inst = block.instructions.get(block.instructions.size() - 1);
                    if (!(inst instanceof IRInstBranch) && !(inst instanceof IRInstRet) && !(inst instanceof IRInstJump)) {
                        block.addInst(new IRInstRet("Void"));
                    }
                } else block.addInst(new IRInstRet("Void"));
            }
        }
        else if (node.name.equals("main")) {
            for (var block : function.blocks) {
                if (!block.instructions.isEmpty()) {
                    IRInst inst = block.instructions.get(block.instructions.size() - 1);
                    if (!(inst instanceof IRInstBranch) && !(inst instanceof IRInstRet) && !(inst instanceof IRInstJump)) {
                        block.addInst(new IRInstRet(new IRConstInt(0)));
                    }
                } else block.addInst(new IRInstRet(new IRConstInt(0)));
            }
        }

        root.functions.add(function);

        currentScope = currentScope.parentScope;
        currentFunction = null;
        currentBlock = null;
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
        IRVal cond = getVal(node.condition);
        IRBasicBlock thenBlock = new IRBasicBlock("if.then");
        IRBasicBlock elseBlock = new IRBasicBlock("if.else");
        IRBasicBlock nextBlock = new IRBasicBlock("if.end");
        currentBlock.addInst(new IRInstBranch(cond, thenBlock, elseBlock));

        currentBlock = currentFunction.newBlock(thenBlock);
        currentScope = new Scope(currentScope);
        node.thenStmts.forEach(stmt -> stmt.accept(this));
        currentBlock.addInst(new IRInstJump(nextBlock));
        currentScope = currentScope.parentScope;

        currentBlock = currentFunction.newBlock(elseBlock);
        currentScope = new Scope(currentScope);
        node.elseStmts.forEach(stmt -> stmt.accept(this));
        currentBlock.addInst(new IRInstJump(nextBlock));
        currentScope = currentScope.parentScope;

        currentBlock = currentFunction.newBlock(nextBlock);
    }
    @Override
    public void visit(StmtReturnNode node) {
        if (node.expr != null) {
            node.expr.accept(this);
            currentBlock.addInst(new IRInstRet(getVal(node.expr)));
        }
        else {
            currentBlock.addInst(new IRInstRet("Void"));
        }
    }
    @Override
    public void visit(StmtBreakNode node) {
        currentBlock.addInst(new IRInstJump(currentScope.breakTo));
    }
    @Override
    public void visit(StmtContinueNode node) {
        currentBlock.addInst(new IRInstJump(currentScope.continueTo));
    }
    @Override
    public void visit(StmtForNode node) {
        currentScope = new Scope(currentScope);

        if (node.InitDef != null) node.InitDef.accept(this);
        else node.InitVar.accept(this);
        IRBasicBlock condBlock = new IRBasicBlock("for.cond");
        IRBasicBlock bodyBlock = new IRBasicBlock("for.body");
        IRBasicBlock stepBlock = new IRBasicBlock("for.step");
        IRBasicBlock nextBlock = new IRBasicBlock("for.end");
        currentBlock.addInst(new IRInstJump(condBlock));

        currentBlock = currentFunction.newBlock(condBlock);
        node.cond.accept(this);
        IRVal cond = getVal(node.cond);
        currentBlock.addInst(new IRInstBranch(cond, bodyBlock, nextBlock));

        currentBlock = currentFunction.newBlock(bodyBlock);
        currentScope = new Scope(currentScope);
        currentScope.breakTo = nextBlock;
        currentScope.continueTo = stepBlock;
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentBlock.addInst(new IRInstJump(stepBlock));
        currentScope = currentScope.parentScope;

        currentBlock = currentFunction.newBlock(stepBlock);
        node.step.accept(this);
        currentBlock.addInst(new IRInstJump(condBlock));

        currentScope = currentScope.parentScope;

        currentBlock = currentFunction.newBlock(nextBlock);
    }
    @Override
    public void visit(StmtWhileNode node) {
        IRBasicBlock condBlock = new IRBasicBlock("while.cond");
        IRBasicBlock bodyBlock = new IRBasicBlock("while.body");
        IRBasicBlock nextBlock = new IRBasicBlock("while.end");
        currentBlock.addInst(new IRInstJump(condBlock));

        currentBlock = currentFunction.newBlock(condBlock);
        node.cond.accept(this);
        IRVal cond = getVal(node.cond);
        currentBlock.addInst(new IRInstBranch(cond, bodyBlock, nextBlock));

        currentBlock = currentFunction.newBlock(bodyBlock);
        currentScope = new Scope(currentScope);
        currentScope.breakTo = nextBlock;
        currentScope.continueTo = condBlock;
        node.stmts.forEach(stmt -> stmt.accept(this));
        currentBlock.addInst(new IRInstJump(condBlock));
        currentScope = currentScope.parentScope;

        currentBlock = currentFunction.newBlock(nextBlock);
    }
    @Override
    public void visit(ExprArrayNode node) {
        node.array.accept(this);
        node.index.accept(this);
        IRTemp temp = new IRTemp(getVal(node.array).type);
        currentBlock.addInst(new IRInstGEP(temp, getVal(node.array), getVal(node.index)));
        node.irPtr = temp;
    }
    @Override
    public void visit(ExprAssignNode node) {
        node.rhs.accept(this);
        node.lhs.accept(this);
        currentBlock.addInst(new IRInstStore(getVal(node.rhs), node.lhs.irPtr));
        node.irVal = getVal(node.rhs);
        node.irPtr = node.lhs.irPtr;
    }
    @Override
    public void visit(ExprBasicNode node) {
        if (node.type.equals(IntType)) {
            node.irVal = new IRConstInt(Integer.parseInt(node.str));
        } else if (node.type.equals(BoolType)) {
            node.irVal = new IRConstBool(node.str.equals("true"));
        } else if (node.type.equals(StringType)) {
            IRConstString strPtr = root.addStringConst(node.str.substring(1, node.str.length() - 1));
            node.irVal = new IRTemp(new IRTypePtr(irCharType));
            currentBlock.addInst(new IRInstGEP((IRTemp) node.irVal, strPtr, new IRConstInt(0), new IRConstInt(0)));
        } else if (node.type.equals(NullType)) {
            node.irVal = new IRConstNull();
        } else {
            node.irVal = currentScope.getIRVal("this");
        }
    }
    @Override
    public void visit(ExprBinaryNode node) {
        node.lhs.accept(this);
        if (node.op.equals("&&") || node.op.equals("||")) {
            IRTemp temp = new IRTemp(new IRTypePtr(irBoolType));
            currentBlock.addInst(new IRInstAlloca(temp, irBoolType));
            IRBasicBlock rhsBlock = new IRBasicBlock("boolBinary.rhs");
            IRBasicBlock trueBlock = new IRBasicBlock("boolBinary.true");
            IRBasicBlock falseBlock = new IRBasicBlock("boolBinary.false");
            IRBasicBlock nextBlock = new IRBasicBlock("boolBinary.end");
            if (node.op.equals("&&")) currentBlock.addInst(new IRInstBranch(getVal(node.lhs), rhsBlock, falseBlock));
            else currentBlock.addInst(new IRInstBranch(getVal(node.lhs), trueBlock, rhsBlock));

            currentBlock = currentFunction.newBlock(rhsBlock);
            node.rhs.accept(this);
            currentBlock.addInst(new IRInstBranch(getVal(node.rhs), trueBlock, falseBlock));

            currentBlock = currentFunction.newBlock(trueBlock);
            IRTemp temp_true = new IRTemp(irBoolType);
            currentBlock.addInst(new IRInstIcmp(temp_true, "eq", new IRConstBool(true), new IRConstBool(true)));
            currentBlock.addInst(new IRInstStore(temp_true, temp));
            currentBlock.addInst(new IRInstJump(nextBlock));

            currentBlock = currentFunction.newBlock(falseBlock);
            IRTemp temp_false = new IRTemp(irBoolType);
            currentBlock.addInst(new IRInstIcmp(temp_false, "eq", new IRConstBool(true), new IRConstBool(false)));
            currentBlock.addInst(new IRInstStore(temp_false, temp));
            currentBlock.addInst(new IRInstJump(nextBlock));

            currentBlock = currentFunction.newBlock(nextBlock);
            IRTemp finalVal = new IRTemp(irBoolType);
            currentBlock.addInst(new IRInstLoad(finalVal, temp));
            node.irVal = finalVal;
            return;
        }
        node.rhs.accept(this);
        String op = null;
        if (node.lhs.type.equals(StringType) || node.rhs.type.equals(StringType)) {
            switch (node.op) {
                case "+" -> {
                    IRTemp temp = new IRTemp(irStringType);
                    currentBlock.addInst(new IRInstCall(temp, irStringType, "string.add", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
                case "<" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstCall(temp, irBoolType, "string.lt", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
                case "<=" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstCall(temp, irBoolType, "string.le", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
                case ">" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstCall(temp, irBoolType, "string.gt", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
                case ">=" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstCall(temp, irBoolType, "string.ge", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
                case "==" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstCall(temp, irBoolType, "string.eq", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
                case "!=" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstCall(temp, irBoolType, "string.ne", getVal(node.lhs), getVal(node.rhs)));
                    node.irVal = temp;
                }
            }
        } else {
            IRVal lhs = getVal(node.lhs), rhs = getVal(node.rhs);
            switch (node.op) {
                case "+" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val + ((IRConstInt) rhs).val);
                    op = "add";
                }
                case "-" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val - ((IRConstInt) rhs).val);
                    op = "sub";
                }
                case "*" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val * ((IRConstInt) rhs).val);
                    op = "mul";
                }
                case "/" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst && ((IRConstInt) rhs).val != 0)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val / ((IRConstInt) rhs).val);
                    op = "sdiv";
                }
                case "%" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val % ((IRConstInt) rhs).val);
                    op = "srem";
                }
                case "<<" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val << ((IRConstInt) rhs).val);
                    op = "shl";
                }
                case ">>" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val >> ((IRConstInt) rhs).val);
                    op = "ashr";
                }
                case "&" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val & ((IRConstInt) rhs).val);
                    op = "and";
                }
                case "|" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val | ((IRConstInt) rhs).val);
                    op = "or";
                }
                case "^" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstInt(((IRConstInt) lhs).val ^ ((IRConstInt) rhs).val);
                    op = "xor";
                }
                case "<" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstBool(((IRConstInt) lhs).val < ((IRConstInt) rhs).val);
                    op = "slt";
                }
                case "<=" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstBool(((IRConstInt) lhs).val <= ((IRConstInt) rhs).val);
                    op = "sle";
                }
                case ">" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstBool(((IRConstInt) lhs).val > ((IRConstInt) rhs).val);
                    op = "sgt";
                }
                case ">=" -> {
                    if (lhs instanceof IRConst && rhs instanceof IRConst)
                        node.irVal = new IRConstBool(((IRConstInt) lhs).val >= ((IRConstInt) rhs).val);
                    op = "sge";
                }
                case "==" -> {
                    if (lhs instanceof IRConstInt && rhs instanceof IRConstInt)
                        node.irVal = new IRConstBool(((IRConstInt) lhs).val == ((IRConstInt) rhs).val);
                    op = "eq";
                }
                case "!=" -> {
                    if (lhs instanceof IRConstInt && rhs instanceof IRConstInt)
                        node.irVal = new IRConstBool(((IRConstInt) lhs).val != ((IRConstInt) rhs).val);
                    op = "ne";
                }
            }
            if (node.irVal != null) return;
            switch (node.op) {
                case "+", "-", "*", "/", "%", "<<", ">>", "&", "|", "^" -> {
                    IRTemp temp = new IRTemp(irIntType);
                    currentBlock.addInst(new IRInstBinary(temp, op, lhs, rhs));
                    node.irVal = temp;
                }
                case "<", "<=", ">", ">=", "==", "!=" -> {
                    IRTemp temp = new IRTemp(irBoolType);
                    currentBlock.addInst(new IRInstIcmp(temp, op, lhs, rhs));
                    node.irVal = temp;
                }
            }
        }
    }
    @Override
    public void visit(ExprCondNode node) {

    }
    @Override
    public void visit(ExprFuncNode node) {
        node.funcName.accept(this);
        DefFuncNode funcDef = node.funcName.function;
        String funcName = funcDef.className == null ? funcDef.name : funcDef.className + "." + funcDef.name;
        IRType returnType = funcDef.returnType.typeTrans(structTypeMap);
        IRInstCall call = new IRInstCall(funcName);
        call.returnType = returnType;

        if (funcDef == ArraySizeFunc) {
            IRTemp array = ((ExprMemberNode) node.funcName).objAddr;
            IRTemp tmp1, tmp2 = new IRTemp(irIntPtrType);
            if (array.type.toString().equals("i32*"))
                tmp1 = array;
            else {
                tmp1 = new IRTemp(irIntPtrType);
                currentBlock.addInst(new IRInstBitcast(tmp1, array));
            }
            currentBlock.addInst(new IRInstGEP(tmp2, tmp1, new IRConstInt(-1)));
            node.irVal = new IRTemp(irIntType);
            currentBlock.addInst(new IRInstLoad((IRTemp) node.irVal, tmp2));
        } else {
            if (funcDef == StringLengthFunc) call.funcName = "strlen";
            if (funcDef.className != null) {
                if (node.funcName instanceof ExprMemberNode)
                    call.args.add(((ExprMemberNode) node.funcName).objAddr);
                else {
                    IRTemp thisPtr = (IRTemp) currentScope.getIRVal("this");
                    IRTemp thisVal = new IRTemp(((IRTypePtr) thisPtr.type).PtrToType());
                    currentBlock.addInst(new IRInstLoad(thisVal, thisPtr));
                    call.args.add(thisVal);
                }
            }
            if (node.args != null) {
                node.args.accept(this);
                node.args.exprs.forEach(arg -> call.args.add(getVal(arg)));
            }
            if (returnType != irVoidType)
                call.callReg = new IRTemp(returnType);
            currentBlock.addInst(call);
            node.irVal = call.callReg;
        }
    }
    @Override
    public void visit(ExprListNode node) {
        node.exprs.forEach(expr -> expr.accept(this));
    }
    @Override
    public void visit(ExprMemberNode node) {
        node.obj.accept(this);
        IRType objType = getVal(node.obj).type;
        node.objAddr = (IRTemp) node.obj.irVal;
        objType = ((IRTypePtr) objType).PtrToType();
        if (objType instanceof IRTypeStruct) {
            IRType memberType = ((IRTypeStruct) objType).getMemberType(node.member);
            if (memberType != null) {
                node.irPtr = new IRTemp(new IRTypePtr(memberType));
                currentBlock.addInst(new IRInstGEP((IRTemp) node.irPtr, getVal(node.obj), new IRConstInt(0),
                        new IRConstInt(((IRTypeStruct) objType).memberOffset.get(node.member))));
            }
        }
    }
    @Override
    public void visit(ExprNewNode node) {
        IRType type = node.type.typeTrans(structTypeMap);
        if (node.type.dim == 0) {
            IRTypeStruct classType = (IRTypeStruct) ((IRTypePtr) type).PtrToType();
            IRTemp callReg = new IRTemp(irStringType);
            currentBlock.
                    addInst(new IRInstCall(callReg, irStringType, "malloc", new IRConstInt(classType.size)));
            node.irVal = new IRTemp(type);
            currentBlock.addInst(new IRInstBitcast(callReg, node.irVal));
            if (classType.hasConstructor)
                currentBlock.
                        addInst(new IRInstCall(classType.typeName + "." + classType.typeName, node.irVal));
        }
        else if (node.sizeList.size() == 0)
            node.irVal = new IRConstNull(type);
        else
            node.irVal = newArray(type, 0, node.sizeList);
    }
    @Override
    public void visit(ExprPreAddNode node) {
        node.expr.accept(this);
        String op = null;
        switch (node.op) {
            case "++"-> {
                op = "add";
                IRVal val = getVal(node.expr);
                IRTemp temp = new IRTemp(irIntType);
                currentBlock.addInst(new IRInstBinary(temp, op, val, new IRConstInt(1)));
                currentBlock.addInst(new IRInstStore(temp, node.expr.irPtr));
                node.irVal = temp;
                node.irPtr = node.expr.irPtr;
            }
            case "--" -> {
                op = "sub";
                IRVal val = getVal(node.expr);
                IRTemp temp = new IRTemp(irIntType);
                currentBlock.addInst(new IRInstBinary(temp, op, val, new IRConstInt(1)));
                currentBlock.addInst(new IRInstStore(temp, node.expr.irPtr));
                node.irVal = temp;
                node.irPtr = node.expr.irPtr;
            }
        }
    }
    @Override
    public void visit(ExprUnaryNode node) {
        node.expr.accept(this);
        String op = null;
        switch (node.op) {
            case "++" -> {
                op = "add";
                node.irVal = getVal(node.expr);
                IRTemp temp = new IRTemp(irIntType);
                currentBlock.addInst(new IRInstBinary(temp, op, node.irVal, new IRConstInt(1)));
                currentBlock.addInst(new IRInstStore(temp, node.expr.irPtr));
            }
            case "--" -> {
                op = "sub";
                node.irVal = getVal(node.expr);
                IRTemp temp = new IRTemp(irIntType);
                currentBlock.addInst(new IRInstBinary(temp, op, node.irVal, new IRConstInt(1)));
                currentBlock.addInst(new IRInstStore(temp, node.expr.irPtr));
            }
            case "+" -> node.irVal = getVal(node.expr);
            case "-" -> {
                op = "sub";
                IRVal val = getVal(node.expr);
                IRTemp temp = new IRTemp(irIntType);
                currentBlock.addInst(new IRInstBinary(temp, op, new IRConstInt(0), val));
                node.irVal = temp;
            }
            case "~" -> {
                op = "xor";
                IRVal val = getVal(node.expr);
                IRTemp temp = new IRTemp(irIntType);
                currentBlock.addInst(new IRInstBinary(temp, op, val, new IRConstInt(-1)));
                node.irVal = temp;
            }
            case "!" -> {
                op = "xor";
                IRVal val = getVal(node.expr);
                IRTemp temp = new IRTemp(irBoolType);
                currentBlock.addInst(new IRInstBinary(temp, op, val, new IRConstBool(true)));
                node.irVal = temp;
            }
        }
    }
    @Override
    public void visit(ExprValNode node) {
        node.irPtr = currentScope.searchIRVal(node.str);
        if (node.irPtr == null) {
            IRTemp thisAddr = (IRTemp) currentScope.searchIRVal("this");
            if (thisAddr != null) {
                IRType objPtrType =  ((IRTypePtr) thisAddr.type).PtrToType();
                IRType objRealType = ((IRTypePtr) objPtrType).PtrToType();
                IRTemp thisVal = new IRTemp("this", objPtrType);
                if (((IRTypeStruct) objRealType).hasMember(node.str)) {
                    currentBlock.addInst(new IRInstLoad(thisVal, thisAddr));
                    node.irPtr = new IRTemp("this." + node.str,
                            new IRTypePtr(((IRTypeStruct) objRealType).getMemberType(node.str)));
                    currentBlock.addInst(new IRInstGEP((IRTemp) node.irPtr, thisVal, new IRConstInt(0),
                            new IRConstInt(((IRTypeStruct) objRealType).memberOffset.get(node.str))));
                }
            }
        }
    }

    public IRVal getVal(ExprNode node) {
        if (node.irVal != null)
            return node.irVal;
        else {
            IRTemp val = new IRTemp(((IRTypePtr) node.irPtr.type).PtrToType());
            currentBlock.addInst(new IRInstLoad(val, node.irPtr));
            return node.irVal = val;
        }
    }
    public IRVal newArray(IRType type, int at, ArrayList<ExprNode> sizeList) {
        IRTemp callReg = new IRTemp(new IRTypePtr(irCharType));
        sizeList.get(at).accept(this);
        IRVal cnt = getVal(sizeList.get(at)), size;
        int sizeOfType = ((IRTypePtr) type).PtrToType().size;

        if (cnt instanceof IRConstInt) {
            size = new IRConstInt(((IRConstInt) cnt).val * sizeOfType + 4);
        } else {
            IRConstInt typeSize = new IRConstInt(sizeOfType);
            IRTemp tmpSize = new IRTemp(irIntType);
            currentBlock.addInst(new IRInstBinary(tmpSize, "mul", cnt, typeSize));
            size = new IRTemp(irIntType);
            currentBlock.addInst(new IRInstBinary((IRTemp) size, "add", tmpSize, new IRConstInt(4)));
        }
        currentBlock.addInst(new IRInstCall(callReg, new IRTypePtr(irCharType), "malloc", size));

        IRTemp ptr, tmp1 = new IRTemp(irIntPtrType), tmp2 = new IRTemp(irIntPtrType);
        currentBlock.addInst(new IRInstBitcast(tmp1, callReg));
        currentBlock.addInst(new IRInstStore(cnt, tmp1));
        currentBlock.addInst(new IRInstGEP(tmp2, tmp1, new IRConstInt(1)));
        if (type.toString().equals("i32*")) ptr = tmp2;
        else {
            ptr = new IRTemp(type);
            currentBlock.addInst(new IRInstBitcast(ptr, tmp2));
        }

        if (at + 1 < sizeList.size()) {
            IRTemp idx = new IRTemp(irIntPtrType);
            currentBlock.addInst(new IRInstAlloca(idx, irIntType));
            currentBlock.addInst(new IRInstStore(new IRConstInt(0), idx));
            IRBasicBlock condBlock = new IRBasicBlock("new.for.cond");
            IRBasicBlock bodyBlock = new IRBasicBlock("new.for.body");
            IRBasicBlock stepBlock = new IRBasicBlock("new.for.step");
            IRBasicBlock nextBlock = new IRBasicBlock("new.for.end");
            currentBlock.addInst(new IRInstJump(condBlock));

            currentBlock = currentFunction.newBlock(condBlock);
            IRTemp cond = new IRTemp(irBoolType);
            IRTemp iVal = new IRTemp(irIntType);
            currentBlock.addInst(new IRInstLoad(iVal, idx));
            currentBlock.addInst(new IRInstIcmp(cond, "slt", iVal, cnt));
            currentBlock.addInst(new IRInstBranch(cond, bodyBlock, nextBlock));

            currentBlock = currentFunction.newBlock(bodyBlock);
            IRVal iPtrVal = newArray(((IRTypePtr) type).PtrToType(), at + 1, sizeList);
            IRTemp iPtr = new IRTemp(type);
            IRTemp iVal2 = new IRTemp(irIntType);
            currentBlock.addInst(new IRInstLoad(iVal2, idx));
            currentBlock.addInst(new IRInstGEP(ptr, iPtr, iVal2));
            currentBlock.addInst(new IRInstStore(iPtrVal, iPtr));
            currentBlock.addInst(new IRInstJump(stepBlock));

            currentBlock = currentFunction.newBlock(stepBlock);
            IRTemp iRes = new IRTemp(irIntType);
            IRTemp iVal3 = new IRTemp(irIntType);
            currentBlock.addInst(new IRInstLoad(iVal3, idx));
            currentBlock.addInst(new IRInstBinary(iRes, "add", iVal3, new IRConstInt(1)));
            currentBlock.addInst(new IRInstStore(iRes, idx));
            currentBlock.addInst(new IRInstJump(condBlock));

            currentBlock = currentFunction.newBlock(nextBlock);
        }
        return ptr;
    }
    public IRVal defaultVal(IRType type) {
        if (type instanceof IRTypeInt) return new IRConstInt(0);
        else return new IRConstNull(type);
    }
}
