package backend;

import ASM.*;
import ASM.inst.*;
import ASM.register.*;
import IR.*;
import IR.inst.*;
import IR.val.*;
import IR.type.*;
import util.Local;

public class ASMBuilder implements IRVisitor, Local {
    public ASMRoot root;
    public ASMFunc currentFunction = null;
    public ASMBlock currentBlock = null;
    public ASMBuilder(ASMRoot root) {
        this.root = root;
    }
    @Override
    public void visit(IRRoot node) {
        for (var val : node.variables) {
            val.asmReg = new ASMGlobalVal(val);
            root.globalValues.add((ASMGlobalVal) val.asmReg);
        }
        for (var str : node.stringConst.values()) {
            ASMGlobalString globalStr = new ASMGlobalString(".str." + String.valueOf(str.id), str.val);
            root.globalStrings.add(globalStr);
            str.asmReg = globalStr;
        }
        for (var func : node.functions) func.accept(this);
    }
    @Override
    public void visit(IRFunction node) {
        currentFunction = new ASMFunc(node.name);
        root.functions.add(currentFunction);
        int callArgs = 0;
        ASMVirtualReg.cnt = 0;
        for (var block : node.blocks) {
            block.asmBlock = new ASMBlock();
            for (var inst : block.instructions) {
                if (inst instanceof IRInstCall) callArgs = Math.max(callArgs, ((IRInstCall) inst).args.size());
            }
        }
        currentFunction.paramSpace = (callArgs > 8 ? callArgs - 8 : 0) * 4;
        for (int i = 0; i < node.params.size(); ++i)
            if (i < 8)
                node.params.get(i).asmReg = ASMPhysicalReg.regMap.get("a" + i);
            else
                node.params.get(i).asmReg = new ASMVirtualReg(i);
        for (int i = 0; i < node.blocks.size(); ++i) {
            currentBlock = node.blocks.get(i).asmBlock;
            if (i == 0) {
                currentBlock.addInst(new ASMInstAddi(ASMPhysicalReg.regMap.get("sp"), ASMPhysicalReg.regMap.get("sp"), 0));
                currentBlock.addInst(new ASMInstStore(4,
                        ASMPhysicalReg.regMap.get("sp"),
                        ASMPhysicalReg.regMap.get("ra"),
                        currentFunction.paramSpace));
            }
            node.blocks.get(i).accept(this);
            currentFunction.addBlock(currentBlock);
        }
        currentFunction.vRegSpace = ASMVirtualReg.cnt;
        currentFunction.totalSpace = currentFunction.paramSpace + currentFunction.allocaSpace + currentFunction.vRegSpace * 4;
        currentFunction.blocks.get(0).instructions.get(0).imm = -currentFunction.totalSpace;
        for (var block : currentFunction.blocks) {
            for (int i = 0; i < block.instructions.size(); i++) {
                if (block.instructions.get(i) instanceof ASMInstRet) block.instructions.get(i - 1).imm = currentFunction.totalSpace;
            }
        }
    }
    @Override
    public void visit(IRBasicBlock node) {
        node.instructions.forEach(inst -> inst.accept(this));
    }
    @Override
    public void visit(IRInstAlloca node) {
        currentBlock.addInst(new ASMInstBinary("add", getReg(node.val), ASMPhysicalReg.regMap.get("sp"),
                new ASMVirtualImm(currentFunction.paramSpace + currentFunction.allocaSpace)));
        currentFunction.allocaSpace += 4;
    }
    @Override
    public void visit(IRInstBinary node) {
        currentBlock.addInst(new ASMInstBinary(node.op, getReg(node.dest), getReg(node.lhs), getReg(node.rhs)));
    }
    @Override
    public void visit(IRInstBranch node) {
        currentBlock.addInst(new ASMInstBranch(getReg(node.cond), node.elseBlock.asmBlock));
        currentBlock.addInst(new ASMInstJump(node.thenBlock.asmBlock));
    }
    @Override
    public void visit(IRInstBitcast node) {
        currentBlock.addInst(new ASMInstMove(getReg(node.dest), getReg(node.val)));
    }
    @Override
    public void visit(IRInstCall node) {
        for (int i = 0; i < node.args.size(); ++i) {
            IRVal arg = node.args.get(i);
            if (i < 8)
                currentBlock.addInst(new ASMInstMove(ASMPhysicalReg.regMap.get("a" + i), getReg(arg)));
            else
                currentBlock.addInst(new ASMInstStore(arg.type.size,
                        ASMPhysicalReg.regMap.get("sp"),
                        getReg(arg),
                        (i - 8) * 4));
        }
        currentBlock.addInst(new ASMInstCall(node.funcName));
        if (node.Ret != null)
            currentBlock.addInst(new ASMInstMove(getReg(node.Ret), ASMPhysicalReg.regMap.get("a0")));
    }
    @Override
    public void visit(IRInstGEP node) {
        IRType type = ((IRTypePtr) node.ptr.type).PtrToType();
        if (type == irBoolType) {
            currentBlock.addInst(new ASMInstBinary("add", getReg(node.dest), getReg(node.ptr), getReg(node.index1)));
        } else {
            ASMReg idx = type instanceof IRTypeStruct ? getReg(node.index2) : getReg(node.index1);
            ASMVirtualReg tmp = new ASMVirtualReg(4);
            currentBlock.addInst(new ASMInstUnary("slli", tmp, idx, 2));
            currentBlock.addInst(new ASMInstBinary("add", getReg(node.dest), getReg(node.ptr), tmp));
        }
    }
    @Override
    public void visit(IRInstIcmp node) {
        ASMVirtualReg tmp = new ASMVirtualReg(4);
        switch (node.op) {
            case "eq" -> {
                currentBlock.addInst(new ASMInstBinary("sub", tmp, getReg(node.lhs), getReg(node.rhs)));
                currentBlock.addInst(new ASMInstUnary("seqz", getReg(node.dest), tmp));
            }
            case "ne" -> {
                currentBlock.addInst(new ASMInstBinary("sub", tmp, getReg(node.lhs), getReg(node.rhs)));
                currentBlock.addInst(new ASMInstUnary("snez", getReg(node.dest), tmp));
            }
            case "sgt" ->
                    currentBlock.addInst(new ASMInstBinary("slt", getReg(node.dest), getReg(node.rhs), getReg(node.lhs)));
            case "sge" -> {
                currentBlock.addInst(new ASMInstBinary("slt", tmp, getReg(node.lhs), getReg(node.rhs)));
                currentBlock.addInst(new ASMInstUnary("xori", getReg(node.dest), tmp, 1));
            }
            case "slt" ->
                    currentBlock.addInst(new ASMInstBinary("slt", getReg(node.dest), getReg(node.lhs), getReg(node.rhs)));
            case "sle" -> {
                currentBlock.addInst(new ASMInstBinary("slt", tmp, getReg(node.rhs), getReg(node.lhs)));
                currentBlock.addInst(new ASMInstUnary("xori", getReg(node.dest), tmp, 1));
            }
        }
    }
    @Override
    public void visit(IRInstJump node) {
        currentBlock.addInst(new ASMInstJump(node.to.asmBlock));
    }
    @Override
    public void visit(IRInstLoad node) {
        currentBlock.addInst(new ASMInstLoad(node.dest.type.size,
                getReg(node.dest),
                getReg(node.from),
                0));
    }
    @Override
    public void visit(IRInstRet node) {
        if (node.retVal != irVoidRetVal)
            currentBlock.addInst(new ASMInstMove(ASMPhysicalReg.regMap.get("a0"), getReg(node.retVal)));
        currentBlock.addInst(new ASMInstLoad(4,
                ASMPhysicalReg.regMap.get("ra"),
                ASMPhysicalReg.regMap.get("sp"),
                currentFunction.paramSpace));
        currentBlock.addInst(new ASMInstAddi(ASMPhysicalReg.regMap.get("sp"), ASMPhysicalReg.regMap.get("sp"), 0));
        currentBlock.addInst(new ASMInstRet());
    }
    @Override
    public void visit(IRInstStore node) {
        currentBlock.addInst(new ASMInstStore(node.val.type.size,
                getReg(node.to),
                getReg(node.val),
                0));
    }

    @Override
    public void visit(IRInstPhi node) {

    }

    public ASMReg getReg(IRVal val) {
        if (val.asmReg == null) {
            if (val instanceof IRTemp || val instanceof IRVariable) {
                val.asmReg = new ASMVirtualReg(val.type.size);
            } else if (val instanceof IRConst) {
                val.asmReg = new ASMVirtualImm((IRConst) val);
            }
        }
        return val.asmReg;
    }
}
