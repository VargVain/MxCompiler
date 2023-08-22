package IR.inst;

import IR.type.IRType;
import IR.val.IRTemp;
import IR.val.IRVal;

import java.util.ArrayList;
import java.util.Arrays;

public class IRInstCall extends IRInst{
    public IRType returnType;
    public ArrayList<IRVal> args = new ArrayList<>();
    public IRTemp callReg;
    public String funcName;
    public IRInstCall(IRTemp callReg, IRType returnType, String funcName, IRVal... args) {
        this.returnType = returnType;
        this.callReg = callReg;
        this.funcName = funcName;
        this.args.addAll(Arrays.asList(args));
    }
    public IRInstCall(String funcName, IRVal... args) {
        this.returnType = irVoidType;
        this.callReg = null;
        this.funcName = funcName;
        this.args.addAll(Arrays.asList(args));
    }
    @Override
    public String toString() {
        String ret = (callReg != null ? callReg.Name() + " = call " : "call ") + returnType + " @" + funcName + "(";
        for (int i = 0; i < args.size(); ++i) {
            ret += args.get(i).toString();
            if (i != args.size() - 1) ret += ", ";
        }
        ret += ")";
        return ret;
    }
}
