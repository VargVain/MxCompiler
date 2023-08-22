package IR.val;

import IR.type.IRType;
import util.Local;

abstract public class IRVal implements Local {
    public IRType type;
    public IRVal(IRType type) {
        this.type = type;
    }
    abstract public String toString();
    abstract public String Name();
}
