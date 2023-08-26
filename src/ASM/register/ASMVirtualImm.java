package ASM.register;

import IR.val.IRConst;
import IR.val.IRConstInt;
import IR.val.IRConstBool;

public class ASMVirtualImm extends ASMReg{
    public int value;
    public ASMVirtualImm(int value) {
        this.value = value;
    }
    public ASMVirtualImm(IRConst constVal) {
        if (constVal instanceof IRConstInt) {
            value = ((IRConstInt) constVal).val;
        } else if (constVal instanceof IRConstBool) {
            value = ((IRConstBool) constVal).val ? 1 : 0;
        } else {
            value = 0;
        }
    }
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
