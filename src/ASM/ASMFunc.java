package ASM;

import java.util.ArrayList;

public class ASMFunc {
    public String name;
    public ArrayList<ASMBlock> blocks = new ArrayList<>();
    public int vRegSpace = 0;
    public int allocaSpace = 4;
    public int paramSpace = 0;
    public int totalSpace = 0;
    public ASMFunc(String name) {
        this.name = name;
    }
    public void addBlock(ASMBlock block) {
        blocks.add(block);
    }
    public String toString() {
        String ret = "  .text\n";
        ret +="  .globl " + name + "\n";
        ret += name + ":\n";
        for (ASMBlock block : blocks)
            ret += block;
        return ret;
    }
    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
