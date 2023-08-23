package IR.inst;

import IR.val.IRVal;

public class IRInstRet extends IRInst{
    public IRVal retVal;
    public IRInstRet(IRVal retVal) {
        this.retVal = retVal;
    }
    @Override
    public String toString() {
        return "ret " + retVal;
    }
}
