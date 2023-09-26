package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMPhysicalReg;
import ASM.register.ASMReg;

import java.util.HashSet;

public class ASMInstCall extends ASMInst{
    public String funcName;
    public HashSet<ASMReg> use = new HashSet<>();
    static HashSet<ASMReg> def = new HashSet<>(ASMPhysicalReg.callerSave);
    public ASMInstCall(String funcName) {
        this.funcName = funcName;
    }
    @Override
    public HashSet<ASMReg> use() {
        return use;
    }
    @Override
    public HashSet<ASMReg> def() {
        return def;
    }
    @Override
    public String toString() {
        return "call " + funcName;
    }
    @Override
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
