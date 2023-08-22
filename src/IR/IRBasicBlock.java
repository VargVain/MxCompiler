package IR;

import IR.inst.IRInst;

import java.util.ArrayList;

public class IRBasicBlock {
    public String name;
    public static int blockCnt = 0;
    public ArrayList<IRInst> instructions = new ArrayList<>();
    public IRBasicBlock(String string) {
        this.name = string + "_" + blockCnt++;
    }
    public void addInst(IRInst inst) {
        instructions.add(inst);
    }
    @Override
    public String toString() {
        String ret = name + ":\n";
        for (IRInst inst : instructions)
            ret += "  " + inst + "\n";
        return ret;
    }
}
