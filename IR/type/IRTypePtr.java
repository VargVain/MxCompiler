package IR.type;

public class IRTypePtr extends IRType{
    public IRType baseType;
    public int dim = 1;
    public IRTypePtr() {
        super("null", 4);
        this.dim = 0;
    }
    public IRTypePtr(IRType baseType) {
        super(baseType.typeName + "*", 4);
        this.baseType = baseType;
        if (baseType instanceof IRTypePtr) {
            this.baseType = ((IRTypePtr) baseType).baseType;
            this.dim = ((IRTypePtr) baseType).dim + 1;
        }
    }
    public IRTypePtr(IRType baseType, int dim) {
        super(baseType.typeName + "*".repeat(dim), 4);
        if (baseType instanceof IRTypePtr) {
            this.baseType = ((IRTypePtr) baseType).baseType;
            this.dim = ((IRTypePtr) baseType).dim + dim;
        } else {
            this.baseType = baseType;
            this.dim = dim;
        }
    }
    public IRType PtrToType() {
        return dim == 1 ? baseType : new IRTypePtr(baseType, dim - 1);
    }
    @Override
    public String toString() {
        return baseType.toString() + "*".repeat(dim);
    }
}
