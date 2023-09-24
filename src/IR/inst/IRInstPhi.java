package IR.inst;

import IR.IRBasicBlock;
import IR.IRVisitor;
import IR.val.*;

import java.util.ArrayList;

public class IRInstPhi extends IRInst{
    public IRTemp val;
    public IRVal variable;
    public ArrayList<IRVal> values = new ArrayList<>();
    public ArrayList<IRBasicBlock> blocks = new ArrayList<>();
    public IRInstPhi(IRTemp val, IRVal variable) {
        this.val = val;
        this.variable = variable;
    }
    public void add(IRVal value, IRBasicBlock block) {
        values.add(value == null ? defaultVal(val.type) : value);
        blocks.add(block);
    }
    @Override
    public String toString() {
        String ret = val.Name() + " = phi " + val.type + " ";
        for (int i = 0; i < values.size(); ++i) {
            ret += "[ " + values.get(i).Name() + ", %" + blocks.get(i).name + " ]";
            if (i != values.size() - 1)
                ret += ", ";
        }
        return ret;
    }
    @Override
    public void replaceUse(IRVal oldVal, IRVal newVal) {
        for (int i = 0; i < values.size(); ++i)
            if (values.get(i) == oldVal)
                values.set(i, newVal);
    }
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
