package util;

public class Type {
    public String typeName;
    public int dim = 0;
    public boolean isClass = false;
    public boolean checkIfTypeDefined(GlobalScope globalScope) {
        switch (typeName) {
            case "int", "bool", "string", "void", "null", "this" -> {
                return true;
            }
            default -> {
                if (globalScope.hasClass(typeName))
                    return true;
            }
        }
        return false;
    }
    public Type(String typeName, int dim) {
        switch (typeName) {
            case "int", "bool", "string", "void", "null", "this" -> {}
            default -> isClass = true;
        }
        this.typeName = typeName;
        this.dim = dim;
    }
    public boolean equals(String typeName, int dim) {
        return this.typeName.equals(typeName) && this.dim == dim;
    }
    public boolean equals(Type type) {
        return this.typeName.equals(type.typeName) && this.dim == type.dim;
    }
    public boolean isRef() {
        return dim > 0 || isClass;
    }
    public boolean isRefOrNull() {
        return dim > 0 || isClass || typeName.equals("null");
    }
    public String toString() {
        return typeName + "[]".repeat(dim);
    }
}
