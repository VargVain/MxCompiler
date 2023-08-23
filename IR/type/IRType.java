package IR.type;

abstract public class IRType {
    public String typeName;
    public int size;
    public IRType(String name, int size) {
        typeName = name;
        this.size = size;
    }
    abstract public String toString();
}
