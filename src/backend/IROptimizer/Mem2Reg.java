package backend.IROptimizer;

import IR.IRBasicBlock;
import IR.IRFunction;
import IR.IRRoot;
import IR.inst.IRInst;
import IR.type.IRTypePtr;
import IR.val.IRTemp;
import IR.val.IRVal;
import IR.inst.*;

import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;

public class Mem2Reg {
    public IRRoot irRoot;
    public HashMap<IRVal, IRVal> replaceMap = new HashMap<>();
    public Mem2Reg(IRRoot irRoot) {
        this.irRoot = irRoot;
        this.irRoot.functions.forEach(this::PlacePhi);
        this.irRoot.functions.forEach(this::Rename);
    }
    public void PlacePhi(IRFunction func) {
        HashSet<IRVal> allVariables = new HashSet<>();
        for (var block : func.blocks) {
            for (var variable : block.orig) {
                variable.defSites.add(block);
                allVariables.add(variable);
            }
        }
        func.allVariables = allVariables;
        for (var variable : allVariables) {
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
        replaceMap.clear();
        RenameBlock(func.blocks.getFirst());
        func.blocks.forEach(this::InsertPhi);
    }
    public void RenameBlock(IRBasicBlock block) {
        var oldReplaceMap = new HashMap<>(replaceMap);
        LinkedList<IRInst> newInst = new LinkedList<>();
        replaceMap.putAll(block.phiMap);

        for (int i = 0; i < block.instructions.size(); ++i) {
            var inst = block.instructions.get(i);
            if (inst instanceof IRInstAlloca)
                continue;
            if (inst instanceof IRInstLoad && replaceMap.containsKey(((IRInstLoad) inst).from)) {
                for (int j = i + 1; j < block.instructions.size(); ++j)
                    block.instructions.get(j).replaceUse(
                            ((IRInstLoad) inst).dest,
                            replaceMap.get(((IRInstLoad) inst).from));
            } else if (inst instanceof IRInstStore && block.orig.contains(((IRInstStore) inst).to)) {
                replaceMap.put(((IRInstStore) inst).to, ((IRInstStore) inst).val);
            } else {
                newInst.add(inst);
            }
        }
        block.instructions = newInst;
        block.succ.forEach(succ -> succ.phis.forEach(phi -> phi.add(replaceMap.get(phi.variable), block)));
        block.children.forEach(this::RenameBlock);
        replaceMap = oldReplaceMap;
    }
    public void InsertPhi(IRBasicBlock block) {
        block.phis.forEach(phi -> block.instructions.addFirst(phi));
    }
}
