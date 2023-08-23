package IR.inst;

import IR.type.IRType;
import IR.val.IRTemp;
import IR.val.IRVal;

import java.util.ArrayList;
import java.util.Arrays;

public class IRInstCall extends IRInst{
    public IRType returnType;
    public ArrayList<IRVal> args = new ArrayList<>();
    public IRTemp Ret;
    public String funcName;
    public IRInstCall(IRTemp Ret, IRType returnType, String funcName, IRVal... args) {
        this.returnType = returnType;
        this.Ret = Ret;
        this.funcName = funcName;
        this.args.addAll(Arrays.asList(args));
    }
    public IRInstCall(String funcName, IRVal... args) {
        this.returnType = irVoidType;
        this.Ret = null;
        this.funcName = funcName;
        this.args.addAll(Arrays.asList(args));
    }
    @Override
    public String toString() {
        String ret = ((Ret != null && !Ret.type.equals(irVoidType)) ? Ret.Name() + " = call " : "call ") + returnType + " @" + funcName + "(";
        for (int i = 0; i < args.size(); ++i) {
            ret += args.get(i).toString();
            if (i != args.size() - 1) ret += ", ";
        }
        ret += ")";
        return ret;
    }
}
