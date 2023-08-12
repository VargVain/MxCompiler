import AST.RootNode;
import antlr.MxLexer;
import antlr.MxParser;
import frontend.ASTBuilder;
import frontend.SemanticChecker;
import frontend.SymbolCollector;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import util.GlobalScope;
import util.antlrErrorListener;

import java.io.FileInputStream;
import java.io.InputStream;

public class Compiler {

    public static void main(String[] args) throws Exception {
        CharStream is = CharStreams.fromStream(System.in);
        MxLexer lexer = new MxLexer(is);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new antlrErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MxParser parser = new MxParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(new antlrErrorListener());
        ParseTree tree = parser.program();
        //AST building
        ASTBuilder astBuilder = new ASTBuilder();
        RootNode root = (RootNode) astBuilder.visit(tree);
        //symbol collection
        GlobalScope globalScope = new GlobalScope();
        SymbolCollector symbolCollector = new SymbolCollector(globalScope);
        symbolCollector.visit(root);
        //semantic
        SemanticChecker semanticChecker = new SemanticChecker(globalScope);
        semanticChecker.visit(root);
    }
}
