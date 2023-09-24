package backend.IROptimizer;

import IR.*;
import IR.inst.*;

public class CFGBuilder {
    public IRRoot irRoot;
    public CFGBuilder(IRRoot irRoot) {
        this.irRoot = irRoot;
        this.irRoot.functions.forEach(this::Build);
    }
    public void Build(IRFunction func) {
        for (var block : func.blocks) {
            IRInst end = block.instructions.getLast();
            if (end instanceof IRInstJump) {
                block.succ.add(((IRInstJump) end).to);
                ((IRInstJump) end).to.pred.add(block);
            }
            else if (end instanceof IRInstBranch) {
                block.succ.add(((IRInstBranch) end).elseBlock);
                block.succ.add(((IRInstBranch) end).thenBlock);
                ((IRInstBranch) end).elseBlock.pred.add(block);
                ((IRInstBranch) end).thenBlock.pred.add(block);
            }
        }
    }
}
