package backend.IROptimizer;

import IR.IRFunction;
import IR.IRRoot;
import IR.inst.*;

public class VarCollector {
    public IRRoot irRoot;
    public VarCollector(IRRoot irRoot) {
        this.irRoot = irRoot;
        this.irRoot.functions.forEach(this::Collect);
    }
    public void Collect(IRFunction func) {
        for (var block : func.blocks) {
            for (var inst : block.instructions) {
                if (inst instanceof IRInstStore st && !irRoot.variables.contains(st.to))
                    block.orig.add(((IRInstStore) inst).to);
            }
        }
    }
}
