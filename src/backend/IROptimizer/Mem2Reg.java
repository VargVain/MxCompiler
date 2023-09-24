package backend.IROptimizer;

import IR.IRBasicBlock;
import IR.IRFunction;
import IR.IRRoot;
import IR.inst.IRInst;
import IR.type.IRTypePtr;
import IR.val.IRTemp;
import IR.val.IRVal;
import IR.inst.*;

import java.util.HashMap;
import java.util.LinkedList;

public class Mem2Reg {
    public IRRoot irRoot;
    public HashMap<IRVal, IRVal> replaceStoreMap = new HashMap<>();
    public HashMap<IRVal, IRVal> replaceLoadMap = new HashMap<>();
    public HashMap<IRVal, IRVal> replaceUseMap = new HashMap<>();
    public Mem2Reg(IRRoot irRoot) {
        this.irRoot = irRoot;
        this.irRoot.functions.forEach(this::PlacePhi);
        this.irRoot.functions.forEach(this::Rename);
    }
    public void PlacePhi(IRFunction func) {
        for (var block : func.blocks) {
            for (var variable : block.orig) {
                variable.defSites.add(block);
            }
        }
        for (var variable : func.allVariables) {
            LinkedList<IRBasicBlock> workList = new LinkedList<>(variable.defSites);
            while (!workList.isEmpty()) {
                IRBasicBlock block = workList.getFirst();
                workList.removeFirst();
                for (var dfBlock : block.domFrontier) {
                    if (!dfBlock.phiMap.containsKey(variable)) {
                        dfBlock.phiMap.put(variable, new IRTemp(((IRTypePtr) variable.type).PtrToType()));
                        if (!dfBlock.orig.contains(variable) && !workList.contains(dfBlock))
                            workList.add(dfBlock);
                    }
                }
            }
        }
        for (var block : func.blocks) {
            block.phiMap.keySet().forEach(variable ->
                    block.phis.add(new IRInstPhi(block.phiMap.get(variable), variable)));
        }
    }
    public void Rename(IRFunction func) {
        replaceStoreMap.clear();
        replaceLoadMap.clear();
        replaceUseMap.clear();
        RenameBlock(func.blocks.getFirst());
        func.blocks.forEach(this::InsertPhi);
    }
    public void RenameBlock(IRBasicBlock block) {
        var oldReplaceStoreMap = new HashMap<>(replaceStoreMap);
        var oldReplaceLoadMap = new HashMap<>(replaceLoadMap);
        var oldReplaceUseMap = new HashMap<>(replaceUseMap);
        LinkedList<IRInst> newInst = new LinkedList<>();
        replaceStoreMap.putAll(block.phiMap);

        for (int i = 0; i < block.instructions.size(); ++i) {
            var inst = block.instructions.get(i);
            if (inst instanceof IRInstAlloca)
                continue;
            replaceLoadMap.keySet().forEach(old -> {
                if (old != null) inst.replaceUse(old, replaceLoadMap.get(old));
            });
            if (inst instanceof IRInstLoad ins && replaceStoreMap.containsKey(ins.from)) {
                replaceLoadMap.put(ins.dest, replaceStoreMap.get(ins.from));
                replaceUseMap.put(ins.from, ins.dest);
            } else if (inst instanceof IRInstStore ins && block.orig.contains(ins.to)) {
                replaceStoreMap.put(ins.to, ins.val);
                //replaceLoadMap.put(replaceUseMap.get(ins.to), ins.val);
            } else {
                newInst.add(inst);
            }
        }
        block.instructions = newInst;
        block.succ.forEach(succ -> succ.phis.forEach(phi -> phi.add(replaceStoreMap.get(phi.variable), block)));
        block.children.forEach(this::RenameBlock);
        replaceStoreMap = oldReplaceStoreMap;
        replaceLoadMap = oldReplaceLoadMap;
        replaceUseMap = oldReplaceUseMap;
    }
    public void InsertPhi(IRBasicBlock block) {
        block.phis.forEach(phi -> block.instructions.addFirst(phi));
    }
}
