package ASM.register;

public class ASMGlobalString extends ASMReg{
    public String name, str;
    public ASMGlobalString(String name, String str) {
        this.name = name;
        this.str = str;
    }
    @Override
    public String toString() {
        String ret = name + ":\n";
        ret += "  .string \"" + str.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\0", "")
                .replace("\t", "\\t")
                .replace("\"", "\\\"") + "\"\n";
        return ret;
    }
}
