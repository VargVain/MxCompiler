package ASM.inst;

import ASM.ASMVisitor;
import ASM.register.ASMReg;

import java.util.HashSet;

abstract public class ASMInst {
    public ASMReg rd, rs1, rs2;
    public int imm = 0;
    public HashSet<ASMReg> use() {
        HashSet<ASMReg> use = new HashSet<>();
        if (rs1 != null) use.add(rs1);
        if (rs2 != null) use.add(rs2);
        return use;
    }
    public HashSet<ASMReg> def() {
        HashSet<ASMReg> def = new HashSet<>();
        if (rd != null) def.add(rd);
        return def;
    }
    @Override
    abstract public String toString();
    abstract public void accept(ASMVisitor visitor);
}
