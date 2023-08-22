package IR.type;

public class IRTypeVoid extends IRType{
    public IRTypeVoid() {
        super("void", 0);
    }
    @Override
    public String toString() {
        return "void";
    }
}
