package ASM.register;

public class ASMVirtualReg extends ASMReg{
    public int id;
    public int size;
    public static int cnt = 0;
    public ASMVirtualReg(int size) {
        this.size = size;
        id = cnt++;
    }
}
