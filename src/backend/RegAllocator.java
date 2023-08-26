package backend;

import ASM.ASMVisitor;
import ASM.*;
import ASM.inst.*;
import ASM.register.*;

import java.util.ArrayList;

public class RegAllocator implements ASMVisitor {
    int totalStack, virtualRegBegin;
    ASMPhysicalReg RegT0 = ASMPhysicalReg.regMap.get("t0");
    ASMPhysicalReg RegT1 = ASMPhysicalReg.regMap.get("t1");
    ASMPhysicalReg RegT2 = ASMPhysicalReg.regMap.get("t2");
    ASMPhysicalReg RegSp = ASMPhysicalReg.regMap.get("sp");
    ArrayList<ASMInst> instructions;
    @Override
    public void visit(ASMRoot node) {
        for (var function : node.functions) {
            function.accept(this);
        }
    }
    @Override
    public void visit(ASMFunc node) {
        totalStack = node.totalSpace;
        virtualRegBegin = node.paramSpace + node.allocaSpace;
        for (var block : node.blocks)
            block.accept(this);
    }
    @Override
    public void visit(ASMBlock node) {
        instructions = new ArrayList<>();
        for (ASMInst inst : node.instructions) {
            if (inst.rs1 != null && !(inst.rs1 instanceof ASMPhysicalReg)) {
                getPhysicalReg(RegT1, inst.rs1, true);
                inst.rs1 = RegT1;
            }
            if (inst.rs2 != null && !(inst.rs2 instanceof ASMPhysicalReg)) {
                getPhysicalReg(RegT0, inst.rs2, true);
                inst.rs2 = RegT0;
            }
            instructions.add(inst);
            if (inst.rd != null && !(inst.rd instanceof ASMPhysicalReg)) {
                getPhysicalReg(RegT0, inst.rd, false);
                inst.rd = RegT0;
            }
        }
        node.instructions = instructions;
    }
    void getPhysicalReg(ASMPhysicalReg Reg, ASMReg vReg, Boolean isSrc) {
        if (vReg instanceof ASMVirtualReg) {
            int offset = virtualRegBegin + ((ASMVirtualReg) vReg).id * 4;
            if (isSrc) {
                instructions.add(new ASMInstLoad(((ASMVirtualReg) vReg).size, Reg, RegSp, offset));
            } else {
                instructions.add(new ASMInstStore(((ASMVirtualReg) vReg).size, RegSp, Reg, offset));
            }
        }
        else if (vReg instanceof ASMVirtualImm) {
            instructions.add(new ASMInstLi(Reg, ((ASMVirtualImm) vReg).value));
        }
        else if (vReg instanceof ASMGlobalVal) {
            String hi = "%hi(" + ((ASMGlobalVal) vReg).name + ")";
            String lo = "%lo(" + ((ASMGlobalVal) vReg).name + ")";
            instructions.add(new ASMInstLui(Reg, hi));
            instructions.add(new ASMInstAddi(Reg, Reg, lo));
        }
        else if (vReg instanceof ASMGlobalString) {
            String hi = "%hi(" + ((ASMGlobalString) vReg).name + ")";
            String lo = "%lo(" + ((ASMGlobalString) vReg).name + ")";
            instructions.add(new ASMInstLui(Reg, hi));
            instructions.add(new ASMInstAddi(Reg, Reg, lo));
        }
    }
    @Override
    public void visit(ASMInstAddi node) {

    }
    @Override
    public void visit(ASMInstBinary node) {

    }
    @Override
    public void visit(ASMInstBranch node) {

    }
    @Override
    public void visit(ASMInstCall node) {

    }
    @Override
    public void visit(ASMInstJump node) {

    }
    @Override
    public void visit(ASMInstLi node) {

    }
    @Override
    public void visit(ASMInstLoad node) {

    }
    @Override
    public void visit(ASMInstLui node) {

    }
    @Override
    public void visit(ASMInstMove node) {

    }
    @Override
    public void visit(ASMInstRet node) {

    }
    @Override
    public void visit(ASMInstStore node) {

    }
    @Override
    public void visit(ASMInstUnary node) {

    }
}
