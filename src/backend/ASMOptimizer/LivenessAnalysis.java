package backend.ASMOptimizer;

import ASM.*;
import ASM.inst.*;
import ASM.register.ASMReg;

import java.util.HashSet;

public class LivenessAnalysis {
    ASMFunc func;
    public LivenessAnalysis(ASMFunc func) {
        this.func = func;
        for (ASMBlock block : func.blocks) {
            block.liveIn.clear();
            block.liveOut.clear();
        }
        for (ASMBlock block : func.blocks) {
            block.use.clear();
            block.def.clear();
            for (ASMInst inst : block.instructions) {
                for (var reg : inst.use())
                    if (!block.def.contains(reg))
                        block.use.add(reg);
                block.def.addAll(inst.def());
            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (var block : func.blocks) {
                HashSet<ASMReg> newLiveIn = new HashSet<>(block.liveOut);
                HashSet<ASMReg> newLiveOut = new HashSet<>();
                newLiveIn.removeAll(block.def);
                newLiveIn.addAll(block.use);
                for (var succBlock : block.succ) {
                    newLiveOut.addAll(succBlock.liveIn);
                }
                if (!newLiveIn.equals(block.liveIn)) {
                    changed = true;
                    block.liveIn = newLiveIn;
                }
                if (!newLiveOut.equals(block.liveOut)) {
                    changed = true;
                    block.liveOut = newLiveOut;
                }
            }
        }
    }
}
