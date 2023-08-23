package IR.val;

import IR.type.IRTypeArray;
import IR.type.IRTypePtr;

public class IRConstString extends IRConst{
    public String val;
    public int id;
    public static int cnt = 0;

    public IRConstString(String val) {
        super(new IRTypePtr(new IRTypeArray(irCharType, val.length() + 1)));
        this.val = val;
        this.id = cnt++;
    }
    public String printStr() {
        String ret = "";
        for (int i = 0; i < val.length(); ++i) {
            char c = val.charAt(i);
            switch (c) {
                case '\n': ret += "\\0A"; break;
                case '\"': ret += "\\22"; break;
                case '\\': ret += "\\\\"; break;
                default: ret += c;
            }
        }
        return ret + "\\00";
    }
    @Override
    public String toString() {
        return "[" + String.valueOf(val.length() + 1) + " x i8]* " + Name();
    }
    @Override
    public String Name() {
        return "@str." + String.valueOf(id);
    }
}
