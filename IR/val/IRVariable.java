package IR.val;

import IR.type.IRType;

public class IRVariable extends IRVal{
    public String name;
    public IRVal initVal;
    public IRVariable(IRType type, String name) {
        super(type);
        this.name = name;
    }
    @Override
    public String toString() {
        return type + " " + name;
    }
    @Override
    public String Name() {return name;}
}
