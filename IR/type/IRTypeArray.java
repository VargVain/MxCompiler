package IR.type;

public class IRTypeArray extends IRType{
    public IRType baseType;
    public int cnt;
    public IRTypeArray(IRType baseType, int cnt) {
        super("[" + String.valueOf(cnt) + " x " + baseType.typeName + "]", baseType.size * cnt);
        this.baseType = baseType;
        this.cnt = cnt;
    }
    @Override
    public String toString() {
        return "[" + String.valueOf(cnt) + " x " + baseType.toString() + "]";
    }

}
