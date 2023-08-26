package IR.inst;

import IR.IRVisitor;
import IR.val.IRVal;
import util.Local;

abstract public class IRInst implements Local {
    abstract public String toString();
    abstract public void accept(IRVisitor visitor);
}
