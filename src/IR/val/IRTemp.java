package IR.val;

import IR.type.IRType;

public class IRTemp extends IRVal{
    public String name;
    public static int TempValCnt = 0;
    public int ValCnt = -1;
    public IRTemp(IRType type) {
        super(type);
        this.name = "";
    }
    public IRTemp(String name, IRType type) {
        super(type);
        this.name = name;
    }
    @Override
    public String toString() {
        if (ValCnt == -1) ValCnt = TempValCnt++;
        return type + " %" + name + "." + ValCnt;
    }
    @Override
    public String Name() {
        if (ValCnt == -1) ValCnt = TempValCnt++;
        return "%" + name + "." + ValCnt;
    }
}
