package io.github.mkoncek.classpathless;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;


public class Fixer {
    public static void main(String args[]){
        ASTParser parser = ASTParser.newParser(AST.JLS_Latest);
        parser.setSource("import java.util.Set; public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }".toCharArray());
        //parser.setSource("/*abc*/".toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        //ASTNode node = parser.createAST(null);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            Set names = new HashSet();

            @Override
            public boolean visit(TypeDeclaration node) {
                var abstractMaker = ASTRewrite.create(cu.getAST());
                abstractMaker.replace(node, cu, null);
                System.out.println("typedecl " + node.getStartPosition());
                System.out.println(node.toString());
                return true;
            }

            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();
                this.names.add(name.getIdentifier());
                System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
                return false; // do not continue to avoid usage info
            }

            public boolean visit(SimpleName node) {
                if (this.names.contains(node.getIdentifier())) {
                    System.out.println("Usage of '" + node + "' at line " + cu.getLineNumber(node.getStartPosition()));
                }
                return true;
            }

        });

        System.out.println(cu.toString());
    }

}
