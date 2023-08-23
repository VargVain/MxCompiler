package IR.val;

import IR.type.IRType;

public class IRTemp extends IRVal{
    public String name;
    public static int TempValCnt = 0;
    public IRTemp(IRType type) {
        super(type);
        this.name = "%." + TempValCnt++;
    }
    public IRTemp(String name, IRType type) {
        super(type);
        this.name = "%." + name + "." + TempValCnt++;
    }
    @Override
    public String toString() {
        return type + " " + name;
    }
    @Override
    public String Name() {return name;}
}
