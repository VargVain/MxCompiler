package IR.type;

import java.util.ArrayList;
import java.util.HashMap;

public class IRTypeStruct extends IRType{
    public ArrayList<IRType> memberType = new ArrayList<>();
    public HashMap<String, Integer> memberOffset = new HashMap<>();
    public boolean hasConstructor = false;

    public IRTypeStruct(String name, int size) {
        super(name, size);
    }

    public void addMember(String name, IRType type) {
        memberType.add(type);
        memberOffset.put(name, memberType.size() - 1);
    }

    public boolean hasMember(String name) {
        return memberOffset.containsKey(name);
    }

    public IRType getMemberType(String name) {
        return hasMember(name) ? memberType.get(memberOffset.get(name)) : null;
    }

    @Override
    public String toString() {
        return "%struct." + typeName;
    }
}
