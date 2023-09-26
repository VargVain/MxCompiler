package ASM.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ASMPhysicalReg extends ASMReg{
    public String name;
    public int id;
    public static HashMap<String, ASMPhysicalReg> regMap = new HashMap<>() {
        {
            put("zero", new ASMPhysicalReg("zero", 0));
            put("ra", new ASMPhysicalReg("ra", 1));
            put("sp", new ASMPhysicalReg("sp", 2));
            put("gp", new ASMPhysicalReg("gp", 3));
            put("tp", new ASMPhysicalReg("tp", 4));
            put("t0", new ASMPhysicalReg("t0", 5));
            put("t1", new ASMPhysicalReg("t1", 6));
            put("t2", new ASMPhysicalReg("t2", 7));
            put("s0", new ASMPhysicalReg("s0", 8));
            put("s1", new ASMPhysicalReg("s1", 9));
            put("a0", new ASMPhysicalReg("a0", 10));
            put("a1", new ASMPhysicalReg("a1", 11));
            put("a2", new ASMPhysicalReg("a2", 12));
            put("a3", new ASMPhysicalReg("a3", 13));
            put("a4", new ASMPhysicalReg("a4", 14));
            put("a5", new ASMPhysicalReg("a5", 15));
            put("a6", new ASMPhysicalReg("a6", 16));
            put("a7", new ASMPhysicalReg("a7", 17));
            put("s2", new ASMPhysicalReg("s2", 18));
            put("s3", new ASMPhysicalReg("s3", 19));
            put("s4", new ASMPhysicalReg("s4", 20));
            put("s5", new ASMPhysicalReg("s5", 21));
            put("s6", new ASMPhysicalReg("s6", 22));
            put("s7", new ASMPhysicalReg("s7", 23));
            put("s8", new ASMPhysicalReg("s8", 24));
            put("s9", new ASMPhysicalReg("s9", 25));
            put("s10", new ASMPhysicalReg("s10", 26));
            put("s11", new ASMPhysicalReg("s11", 27));
            put("t3", new ASMPhysicalReg("t3", 28));
            put("t4", new ASMPhysicalReg("t4", 29));
            put("t5", new ASMPhysicalReg("t5", 30));
            put("t6", new ASMPhysicalReg("t6", 31));
        }
    };
    public static LinkedList<ASMPhysicalReg> color2reg = new LinkedList<>() {
        {
            add(regMap.get("zero"));
            add(regMap.get("ra"));
            add(regMap.get("sp"));
            add(regMap.get("gp"));
            add(regMap.get("tp"));
            for (int i = 0; i < 3; i++) add(regMap.get("t" + i));
            for (int i = 0; i < 2; i++) add(regMap.get("s" + i));
            for (int i = 0; i < 8; i++) add(regMap.get("a" + i));
            for (int i = 2; i < 12; i++) add(regMap.get("s" + i));
            for (int i = 3; i < 7; i++) add(regMap.get("t" + i));
        }
    };
    public static HashSet<ASMReg> callerSave = new HashSet<>() {
        {
            add(regMap.get("ra"));
            for (int i = 0; i < 7; i++) add(regMap.get("t" + i));
            for (int i = 0; i < 8; i++) add(regMap.get("a" + i));
        }
    };
    public static HashSet<ASMReg> calleeSave = new HashSet<>() {
        {
            for (int i = 0; i < 12; i++) add(regMap.get("s" + i));
        }
    };
    public ASMPhysicalReg(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String toString() {
        return name;
    }
}
