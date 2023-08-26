package ASM.register;

import IR.val.*;

public class ASMGlobalVal extends ASMReg{
    public String name;
    public int word = 0, size;
    public ASMGlobalVal(IRVariable var) {
        this.name = var.name.substring(1);
        size = var.initVal.type.size;
        if (var.initVal instanceof IRConstInt) {
            word = ((IRConstInt) var.initVal).val;
        } else if (var.initVal instanceof IRConstBool) {
            word = ((IRConstBool) var.initVal).val ? 1 : 0;
        } else if (var.initVal instanceof IRConstNull) {
            word = 0;
        }
    }
    @Override
    public String toString() {
        String ret = name + ":\n";
        ret += (size == 4 ? "  .word " : "  .byte ") + word + "\n";
        return ret;
    }
}
