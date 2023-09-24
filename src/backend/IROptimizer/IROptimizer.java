package backend.IROptimizer;

import IR.*;

public class IROptimizer {
    public IROptimizer(IRRoot irRoot) {
        new CFGBuilder(irRoot);
        new ComputeDF(irRoot);
        new VarCollector(irRoot);
        new Mem2Reg(irRoot);
    }
}
