package IR;

import IR.val.IRConstString;
import IR.type.*;
import IR.val.IRVariable;
import util.Local;

import java.util.ArrayList;
import java.util.HashMap;

public class IRRoot implements Local {
    public ArrayList<IRFunction> functions = new ArrayList<>();
    public ArrayList<IRVariable> variables = new ArrayList<>();
    public ArrayList<IRTypeStruct> classes = new ArrayList<>();
    public HashMap<String, IRConstString> stringConst = new HashMap<>();
    public int InitTempCnt = 0;
    public IRFunction globalValInit = new IRFunction(".globalVal.init", irVoidType);
    public IRRoot() {
        globalValInit.newBlock("init");
    }
    public IRConstString addStringConst(String str) {
        String val = "";
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '\\') {
                ++i;
                switch (str.charAt(i)) {
                    case 'n' -> val += '\n';
                    case '\"' -> val += '\"';
                    default -> val += '\\';
                }
            } else val += c;
        }
        if (!stringConst.containsKey(val))
            stringConst.put(val, new IRConstString(val));
        return stringConst.get(val);
    }
    @Override
    public String toString() {
        String ret = "";
        ret += "target datalayout = \"e-m:e-p:32:32-p270:32:32-p271:32:32-p272:64:64-f64:32:64-f80:32-n8:16:32-S128\"\n";
        ret += "target triple = \"i386-pc-linux-gnu\"\n\n";
        for (IRTypeStruct structType : classes) {
            ret += structType + " = type {";
            for (int i = 0; i < structType.memberType.size(); ++i) {
                ret += structType.memberType.get(i);
                if (i != structType.memberType.size() - 1)
                    ret += ", ";
            }
            ret += "}\n";
        }
        for (IRConstString str : stringConst.values())
            ret += "@str." + String.valueOf(str.id) + " = private unnamed_addr constant ["
                    + String.valueOf(str.val.length() + 1) + " x i8] c\"" + str.printStr() + "\"\n";
        for (IRVariable globalVar : variables)
            ret += globalVar.name + " = dso_local global " + ((IRTypePtr) globalVar.type).PtrToType() + " " + globalVar.initVal.Name() + "\n";

        ret += "\ndeclare dso_local i8* @malloc(i32)\n";
        ret += "declare dso_local i32 @strlen(i8*)\n";
        ret += "declare void @print(i8*)\n";
        ret += "declare void @println(i8*)\n";
        ret += "declare void @printInt(i32)\n";
        ret += "declare void @printlnInt(i32)\n";
        ret += "declare i8* @getString()\n";
        ret += "declare i32 @getInt()\n";
        ret += "declare i8* @toString(i32)\n";
        ret += "declare i8* @string.substring(i8*, i32, i32)\n";
        ret += "declare i32 @string.parseInt(i8*)\n";
        ret += "declare i32 @string.ord(i8*, i32)\n";
        ret += "declare i8* @string.add(i8*, i8*)\n";
        ret += "declare i1 @string.lt(i8*, i8*)\n";
        ret += "declare i1 @string.le(i8*, i8*)\n";
        ret += "declare i1 @string.gt(i8*, i8*)\n";
        ret += "declare i1 @string.ge(i8*, i8*)\n";
        ret += "declare i1 @string.eq(i8*, i8*)\n";
        ret += "declare i1 @string.nq(i8*, i8*)\n\n";

        for (IRFunction func : functions)
            ret += func + "\n";
        return ret;
    }
}
