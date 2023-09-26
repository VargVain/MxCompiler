package backend.ASMOptimizer;

import ASM.ASMFunc;
import ASM.inst.*;
import ASM.register.ASMPhysicalReg;
import ASM.register.ASMReg;
import ASM.register.ASMVirtualReg;

import java.util.*;

/*
 * Reference: Modern Compiler Implementation in C
 */

public class RegAllocator {
    public ASMFunc func;
    static int K = 27;
    LinkedHashSet<ASMReg> preColored = new LinkedHashSet<>(ASMPhysicalReg.regMap.values());
    LinkedHashSet<ASMReg> initial = new LinkedHashSet<>();
    LinkedList<ASMReg> simplifyWorkList = new LinkedList<>();
    LinkedList<ASMReg> freezeWorkList = new LinkedList<>();
    LinkedList<ASMReg> spillWorkList = new LinkedList<>();
    LinkedHashSet<ASMReg> spilledNodes = new LinkedHashSet<>();
    public LinkedHashSet<ASMReg> coalescedNodes = new LinkedHashSet<>();
    public LinkedHashSet<ASMReg> coloredNodes = new LinkedHashSet<>();
    public Stack<ASMReg> selectStack = new Stack<>();

    public LinkedHashSet<ASMInstMove> coalescedMoves = new LinkedHashSet<>();
    public LinkedHashSet<ASMInstMove> constrainedMoves = new LinkedHashSet<>();
    public LinkedHashSet<ASMInstMove> frozenMoves = new LinkedHashSet<>();
    public LinkedHashSet<ASMInstMove> workListMoves = new LinkedHashSet<>();
    public LinkedHashSet<ASMInstMove> activeMoves = new LinkedHashSet<>();

    public static class Edge {
        public ASMReg u, v;
        public Edge(ASMReg u, ASMReg v) {
            this.u = u;
            this.v = v;
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge e))
                return false;
            return (u == e.u && v == e.v) || (u == e.v && v == e.u);
        }
        @Override
        public int hashCode() {
            return u.hashCode() ^ v.hashCode();
        }
    }

    public HashSet<Edge> adjSet = new HashSet<>();
    public HashMap<ASMReg, HashSet<ASMReg>> adjList = new HashMap<>();
    public HashMap<ASMReg, Integer> degree = new HashMap<>();
    public HashMap<ASMReg, HashSet<ASMInstMove>> moveList = new HashMap<>();
    public HashMap<ASMReg, ASMReg> alias = new HashMap<>();
    public HashMap<ASMReg, Integer> color = new HashMap<>();
    public HashSet<ASMReg> spillTemp = new HashSet<>();
    public RegAllocator(ASMFunc func) {
        this.func = func;
        while (true) {
            new LivenessAnalysis(func);
            Init();
            Build();
            MakeWorkList();
            while (true) {
                if (!simplifyWorkList.isEmpty()) Simplify();
                else if (!workListMoves.isEmpty()) Coalesce();
                else if (!freezeWorkList.isEmpty()) Freeze();
                else if (!spillWorkList.isEmpty()) SelectSpill();
                if (simplifyWorkList.isEmpty()
                        && workListMoves.isEmpty()
                        && freezeWorkList.isEmpty()
                        && spillWorkList.isEmpty()) break;
            }
            AssignColor();
            if (spilledNodes.isEmpty()) break;
            RewriteProgram();
        }

        for (var block : func.blocks) {
            LinkedList<ASMInst> newInst = new LinkedList<>();
            for (ASMInst inst : block.instructions) {
                if (inst.rd instanceof ASMVirtualReg)
                    inst.rd = ASMPhysicalReg.color2reg.get(color.get(inst.rd));
                if (inst.rs1 instanceof ASMVirtualReg)
                    inst.rs1 = ASMPhysicalReg.color2reg.get(color.get(inst.rs1));
                if (inst.rs2 instanceof ASMVirtualReg)
                    inst.rs2 = ASMPhysicalReg.color2reg.get(color.get(inst.rs2));
                if (!(inst instanceof ASMInstMove) || inst.rd != inst.rs1)
                    newInst.add(inst);
            }
            block.instructions = newInst;
        }
    }
    public void Init() {

        preColored.clear();
        initial.clear();
        simplifyWorkList.clear();
        freezeWorkList.clear();
        spillWorkList.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        coloredNodes.clear();
        selectStack.clear();

        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        workListMoves.clear();
        activeMoves.clear();

        adjSet.clear();
        adjList.clear();
        degree.clear();
        moveList.clear();
        alias.clear();
        color.clear();

        for (var reg : ASMPhysicalReg.regMap.values()) {
            preColored.add(reg);
            adjList.put(reg, new HashSet<>());
            degree.put(reg, 1000);
            moveList.put(reg, new HashSet<>());
            alias.put(reg, null);
            color.put(reg, reg.id);
        }
        for (var block : func.blocks)
            for (var inst : block.instructions) {
                initial.addAll(inst.def());
                initial.addAll(inst.use());
            }
        initial.removeAll(preColored);
        for (var reg : initial) {
            adjList.put(reg, new HashSet<>());
            degree.put(reg, 0);
            moveList.put(reg, new HashSet<>());
            alias.put(reg, null);
            color.put(reg, null);
        }
    }
    public void Build() {
        for (var block : func.blocks) {
            HashSet<ASMReg> live = block.liveOut;
            for (int i = block.instructions.size() - 1; i >= 0; i--) {
                ASMInst inst = block.instructions.get(i);
                if (inst instanceof ASMInstMove mv) {
                    live.removeAll(mv.use());
                    mv.def().forEach(def -> moveList.get(def).add(mv));
                    mv.use().forEach(use -> moveList.get(use).add(mv));
                    workListMoves.add(mv);
                }
                live.addAll(inst.def());
                for (var d : inst.def()) {
                    for (var l : live) {
                        AddEdge(l, d);
                    }
                }
                live.removeAll(inst.def());
                live.addAll(inst.use());
            }
        }
    }
    public void AddEdge(ASMReg u, ASMReg v) {
        Edge e = new Edge(u, v);
        if (u != v && !adjSet.contains(e)) {
            adjSet.add(e);
            if (!preColored.contains(u)) {
                adjList.get(u).add(v);
                degree.put(u, degree.get(u) + 1);
            }
            if (!preColored.contains(v)) {
                adjList.get(v).add(u);
                degree.put(v, degree.get(v) + 1);
            }
        }
    }
    public void MakeWorkList() {
        for (var reg : initial) {
            if (degree.get(reg) >= K)
                spillWorkList.add(reg);
            else if (MoveRelated(reg))
                freezeWorkList.add(reg);
            else
                simplifyWorkList.add(reg);
        }
        initial.clear();
    }
    public boolean MoveRelated(ASMReg reg) {
        return !NodeMoves(reg).isEmpty();
    }
    public HashSet<ASMInstMove> NodeMoves(ASMReg reg) {
        HashSet<ASMInstMove> ret = new HashSet<>(activeMoves);
        ret.addAll(workListMoves);
        ret.retainAll(moveList.get(reg));
        return ret;
    }
    public HashSet<ASMReg> Adjacent(ASMReg reg) {
        HashSet<ASMReg> ret = new HashSet<>(adjList.get(reg));
        ret.removeAll(coalescedNodes);
        ret.removeAll(selectStack);
        return ret;
    }
    public void Simplify() {
        while (!simplifyWorkList.isEmpty()) {
            ASMReg reg = simplifyWorkList.removeFirst();
            selectStack.push(reg);
            Adjacent(reg).forEach(this::DecrementDegree);
        }
    }
    public void DecrementDegree(ASMReg reg) {
        int d = degree.get(reg);
        degree.put(reg, d - 1);
        if (d == K) {
            HashSet<ASMReg> nodes = new HashSet<>(Adjacent(reg));
            nodes.add(reg);
            EnableMoves(nodes);
            spillWorkList.remove(reg);
            if (MoveRelated(reg))
                freezeWorkList.add(reg);
            else
                simplifyWorkList.add(reg);
        }
    }
    public void EnableMoves(HashSet<ASMReg> nodes) {
        for (var reg : nodes) {
            for (var mv : NodeMoves(reg)) {
                if (activeMoves.contains(mv)) {
                    activeMoves.remove(mv);
                    workListMoves.add(mv);
                }
            }
        }
    }
    public void Coalesce() {
        ASMInstMove mv = workListMoves.iterator().next();
        ASMReg x = GetAlias(mv.rd), y = GetAlias(mv.rs1);
        Edge e = preColored.contains(y) ? new Edge(y, x) : new Edge(x, y);
        workListMoves.remove(mv);
        if (e.u == e.v) {
            coalescedMoves.add(mv);
            AddWorkList(e.u);
        }
        else if (preColored.contains(e.v) || adjSet.contains(e)
            || e.u == ASMPhysicalReg.regMap.get("zero")
            || e.v == ASMPhysicalReg.regMap.get("zero")) {
            constrainedMoves.add(mv);
            AddWorkList(e.u);
            AddWorkList(e.v);
        }
        else {
            boolean flag = true;
            for (ASMReg reg : Adjacent(e.v))
                flag &= George(reg, e.u);
            HashSet<ASMReg> uv = new HashSet<>(Adjacent(e.u));
            uv.addAll(Adjacent(e.v));
            if (preColored.contains(e.u) && flag || !preColored.contains(e.u) && Briggs(uv)) {
                coalescedMoves.add(mv);
                Combine(e.u, e.v);
                AddWorkList(e.u);
            } else {
                activeMoves.add(mv);
            }
        }
    }
    boolean George(ASMReg t, ASMReg r) {
        return degree.get(t) < K || preColored.contains(t) || adjSet.contains(new Edge(t, r));
    }

    boolean Briggs(HashSet<ASMReg> uv) {
        int cnt = 0;
        for (ASMReg reg : uv)
            if (degree.get(reg) >= K)
                cnt++;
        return cnt < K;
    }
    public void AddWorkList(ASMReg reg) {
        if (!preColored.contains(reg) && !MoveRelated(reg) && degree.get(reg) < K) {
            freezeWorkList.remove(reg);
            simplifyWorkList.add(reg);
        }
    }
    public ASMReg GetAlias(ASMReg reg) {
        if (coalescedNodes.contains(reg))
            return GetAlias(alias.get(reg));
        return reg;
    }
    public void Combine(ASMReg u, ASMReg v) {
        if (freezeWorkList.contains(v))
            freezeWorkList.remove(v);
        else
            spillWorkList.remove(v);
        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.get(u).addAll(moveList.get(v));
        EnableMoves(new HashSet<>() {{add(v);}});
        for (var t : Adjacent(v)) {
            AddEdge(t, u);
            DecrementDegree(t);
        }
        if (degree.get(u) >= K && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }
    public void Freeze() {
        ASMReg reg = freezeWorkList.removeFirst();
        simplifyWorkList.add(reg);
        FreezeMoves(reg);
    }
    public void FreezeMoves(ASMReg reg) {
        for (var mv : NodeMoves(reg)) {
            ASMReg x = mv.rd, y = mv.rs1, v;
            v = GetAlias(y) == GetAlias(reg) ? GetAlias(x) : GetAlias(y);
            activeMoves.remove(mv);
            frozenMoves.add(mv);
            if (NodeMoves(v).size() == 0 && degree.get(v) < K) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        }
    }
    public void SelectSpill() {
        ASMReg reg = spillWorkList.removeFirst();
        simplifyWorkList.add(reg);
        FreezeMoves(reg);
    }
    public void AssignColor() {
        while (!selectStack.isEmpty()) {
            ASMReg reg = selectStack.pop();
            HashSet<Integer> okColors = new HashSet<>();
            for (int i = 5; i < 32; i++) {
                okColors.add(i);
            }
            for (var adj : adjList.get(reg)) {
                ASMReg adjAlias = GetAlias(adj);
                if (coloredNodes.contains(adjAlias) || preColored.contains(adjAlias))
                    okColors.remove(color.get(adjAlias));
            }
            if (okColors.isEmpty())
                spilledNodes.add(reg);
            else {
                coloredNodes.add(reg);
                color.put(reg, okColors.iterator().next());
            }
        }
        for (ASMReg reg : coalescedNodes)
            color.put(reg, color.get(GetAlias(reg)));
    }
    public void RewriteProgram() {
        for (ASMReg reg : spilledNodes) {
            reg.offset = func.paramSpace + func.allocaSpace + func.spillSpace;
            func.spillSpace += 4;
        }
        for (var block : func.blocks) {
            LinkedList<ASMInst> newInst = new LinkedList<>();
            for (ASMInst inst : block.instructions) {
                ASMVirtualReg same = null;
                if (inst.rs1 != null && inst.rs1.offset != -1) {
                    ASMVirtualReg newReg = new ASMVirtualReg(4);
                    spillTemp.add(newReg);
                    newInst.add(new ASMInstLoad(4, newReg, getReg("sp"), inst.rs1.offset));
                    if (inst.rs1 == inst.rs2)
                        inst.rs2 = newReg;
                    if (inst.rs1 == inst.rd)
                        same = newReg;
                    inst.rs1 = newReg;
                }
                if (inst.rs2 != null && inst.rs2.offset != -1) {
                    ASMVirtualReg newReg = new ASMVirtualReg(4);
                    spillTemp.add(newReg);
                    newInst.add(new ASMInstLoad(4, newReg, getReg("sp"), inst.rs2.offset));
                    if (inst.rs2 == inst.rd)
                        same = newReg;
                    inst.rs2 = newReg;
                }
                newInst.add(inst);
                if (inst.rd != null && inst.rd.offset != -1) {
                    ASMVirtualReg newReg = same == null ? new ASMVirtualReg(4) : same;
                    spillTemp.add(newReg);
                    newInst.add(new ASMInstStore(4, getReg("sp"), newReg, inst.rd.offset));
                    inst.rd = newReg;
                }
            }
            block.instructions = newInst;
        }
    }
    public ASMPhysicalReg getReg(String name) {
        return ASMPhysicalReg.regMap.get(name);
    }
}
