package backend.IROptimizer;

import IR.*;
import IR.inst.*;

import java.util.LinkedList;

public class CFGBuilder {
    public IRRoot irRoot;
    public CFGBuilder(IRRoot irRoot) {
        this.irRoot = irRoot;
        this.irRoot.functions.forEach(this::Build);
        this.irRoot.functions.forEach(this::Check);
        this.irRoot.functions.forEach(this::Clear);
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
    public void Check(IRFunction func) {
        LinkedList<IRBasicBlock> edgeList = new LinkedList<>();
        for (var block : func.blocks) {
            if (block.succ.size() > 1) {
                for (var succBlock : block.succ) {
                    if (succBlock.pred.size() > 1) {
                        IRBasicBlock edgeBlock = new IRBasicBlock("edge");
                        edgeBlock.addInst(new IRInstJump(succBlock));
                        IRInst end = block.instructions.getLast();
                        if (((IRInstBranch) end).thenBlock == succBlock) {
                            ((IRInstBranch) end).thenBlock = edgeBlock;
                        }
                        else ((IRInstBranch) end).elseBlock = edgeBlock;
                        edgeList.add(edgeBlock);
                    }
                }
            }
        }
        edgeList.forEach(edgeBlock -> func.newBlock(edgeBlock));
    }
    public void Clear(IRFunction func) {
        for (var block : func.blocks) {
            block.succ.clear();
            block.pred.clear();
        }
    }
}
