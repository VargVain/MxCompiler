package ASM;

import ASM.register.*;

import java.util.ArrayList;

public class ASMRoot {
    public ArrayList<ASMGlobalVal> globalValues = new ArrayList<>();
    public ArrayList<ASMGlobalString> globalStrings = new ArrayList<>();
    public ArrayList<ASMFunc> functions = new ArrayList<>();
    @Override
    public String toString() {
        String ret = "";
        if (globalValues.size() > 0)
            ret += "  .section .data\n";
        for (var globalValue : globalValues)
            ret += globalValue.toString();
        if (globalStrings.size() > 0)
            ret += "  .section .rodata\n";
        for (var globalString : globalStrings)
            ret += globalString.toString();
        for (var function : functions)
            ret += function.toString();
        return ret;
    }
}
