package backend.ASMOptimizer;

import ASM.ASMRoot;

public class ASMOptimizer {
    public ASMOptimizer(ASMRoot asmRoot) {
        for (var func : asmRoot.functions) {
            new RegAllocator(func);
        }
    }
}
