
import ASM.*;
import AST.RootNode;
import antlr.MxLexer;
import antlr.MxParser;
import backend.ASMOptimizer.ASMOptimizer;
import backend.IROptimizer.IROptimizer;
import frontend.ASTBuilder;
import frontend.SemanticChecker;
import frontend.SymbolCollector;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import util.GlobalScope;
import util.antlrErrorListener;
import IR.*;
import backend.*;

import java.io.FileOutputStream;

public class Compiler {

    public static void main(String[] args) throws Exception {
        CharStream is = CharStreams.fromStream(System.in);
        // CharStream is = CharStreams.fromStream(new FileInputStream("input.mx"));
        MxLexer lexer = new MxLexer(is);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new antlrErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MxParser parser = new MxParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(new antlrErrorListener());
        ParseTree tree = parser.program();
        // [AST building]
        ASTBuilder astBuilder = new ASTBuilder();
        RootNode root = (RootNode) astBuilder.visit(tree);
        // [symbol collection]
        GlobalScope globalScope = new GlobalScope();
        SymbolCollector symbolCollector = new SymbolCollector(globalScope);
        symbolCollector.visit(root);
        // [semantic]
        SemanticChecker semanticChecker = new SemanticChecker(globalScope);
        semanticChecker.visit(root);
        // [IR]
        IRRoot irRoot = new IRRoot();
        new IRBuilder(irRoot, globalScope).visit(root);
        // [IR optimize]
        new IROptimizer(irRoot);
        // [IR output]
        FileOutputStream irOut = new FileOutputStream("output.ll");
        irOut.write(irRoot.toString().getBytes());
        irOut.close();
        // System.out.print(irRoot.toString());
        // [ASM]
        ASMRoot asmRoot = new ASMRoot();
        new ASMBuilder(asmRoot).visit(irRoot);
        // new TempAllocator().visit(asmRoot);
        // [ASM optimize]
        new ASMOptimizer(asmRoot);
        new ExitBlock(asmRoot);
        // [ASM output]
        FileOutputStream out = new FileOutputStream("output.s");
        out.write(asmRoot.toString().getBytes());
        out.close();
        System.out.print(asmRoot.toString());
    }
}
