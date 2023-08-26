package ASM.register;

import java.util.HashMap;

public class ASMPhysicalReg extends ASMReg{
    public String name;
    public static HashMap<String, ASMPhysicalReg> regMap = new HashMap<>() {
        {
            put("zero", new ASMPhysicalReg("zero"));
            put("ra", new ASMPhysicalReg("ra"));
            put("sp", new ASMPhysicalReg("sp"));
            put("t0", new ASMPhysicalReg("t0"));
            put("t1", new ASMPhysicalReg("t1"));
            put("t2", new ASMPhysicalReg("t2"));
            put("a0", new ASMPhysicalReg("a0"));
            put("a1", new ASMPhysicalReg("a1"));
            put("a2", new ASMPhysicalReg("a2"));
            put("a3", new ASMPhysicalReg("a3"));
            put("a4", new ASMPhysicalReg("a4"));
            put("a5", new ASMPhysicalReg("a5"));
            put("a6", new ASMPhysicalReg("a6"));
            put("a7", new ASMPhysicalReg("a7"));
        }
    };
    public ASMPhysicalReg(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
