package IR.inst;

import IR.val.IRVal;

public class IRInstRet extends IRInst{
    public IRVal retVal = null;
    public boolean retVoid = false;
    public IRInstRet(IRVal retVal) {
        this.retVal = retVal;
    }
    public IRInstRet(String voided) {
        this.retVoid = true;
    }
    @Override
    public String toString() {
        return retVoid ? "ret void" : "ret " + retVal;
    }
}
