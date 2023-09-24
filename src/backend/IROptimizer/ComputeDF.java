package backend.IROptimizer;

import IR.IRBasicBlock;
import IR.IRFunction;
import IR.IRRoot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ComputeDF {
    public IRRoot irRoot;
    public HashSet<IRBasicBlock> visited = new HashSet<>();
    public LinkedList<IRBasicBlock> rpo = new LinkedList<>();
    public HashMap<IRBasicBlock, Integer> order = new HashMap<>();
    public ComputeDF(IRRoot irRoot) {
        this.irRoot = irRoot;
        this.irRoot.functions.forEach(this::Build);
    }
    void getRPO(IRBasicBlock block) {
        visited.add(block);
        for (var succBlock : block.succ) {
            if (!visited.contains(succBlock))
                getRPO(succBlock);
        }
        order.put(block, rpo.size());
        rpo.addFirst(block);
    }
    public void Build(IRFunction func) {
        visited.clear();
        rpo.clear();
        order.clear();
        getRPO(func.blocks.getFirst());
        rpo.removeFirst();
        func.blocks.getFirst().idom = func.blocks.getFirst();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (var block : rpo) {
                IRBasicBlock newIdom = null;
                for (var predBlock : block.pred)
                    if (newIdom == null)
                        newIdom = predBlock;
                    else if (predBlock.idom != null)
                        newIdom = intersect(predBlock, newIdom);
                if (newIdom != block.idom) {
                    block.idom = newIdom;
                    changed = true;
                }
            }
        }
        rpo.forEach(block -> block.idom.children.add(block));
        rpo.addFirst(func.blocks.getFirst());
        for (IRBasicBlock block : rpo) {
            if (block.pred.size() >= 2) {
                for (IRBasicBlock predBlock : block.pred) {
                    IRBasicBlock runner = predBlock;
                    while (runner != block.idom) {
                        runner.domFrontier.add(block);
                        runner = runner.idom;
                    }
                }
            }
        }
    }
    IRBasicBlock intersect(IRBasicBlock x, IRBasicBlock y) {
        while (x != y) {
            while (order.get(x) < order.get(y))
                x = x.idom;
            while (order.get(y) < order.get(x))
                y = y.idom;
        }
        return x;
    }
}
