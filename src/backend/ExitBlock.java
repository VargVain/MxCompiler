package backend;

import ASM.ASMRoot;
import ASM.inst.ASMInstRet;

public class ExitBlock {
    public ASMRoot asmRoot;
    public ExitBlock(ASMRoot asmRoot) {
        this.asmRoot = asmRoot;
        for (var func : asmRoot.functions) {
            func.totalSpace = func.paramSpace + func.allocaSpace + func.spillSpace;
            func.blocks.getFirst().instructions.getFirst().imm = -func.totalSpace;
            func.exitBlock.instructions.getLast().imm = func.totalSpace;
            func.exitBlock.addInst(new ASMInstRet());
        }
    }
}
