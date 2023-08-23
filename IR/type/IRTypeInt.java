package IR.type;

public class IRTypeInt extends IRType{
    public int bitWidth;
    public IRTypeInt(int bitWidth) {
        super("i" + String.valueOf(bitWidth), bitWidth / 8);
        if (size == 0) size = 1;
        this.bitWidth = bitWidth;
    }
    @Override
    public String toString() {
        return "i" + String.valueOf(bitWidth);
    }
}
