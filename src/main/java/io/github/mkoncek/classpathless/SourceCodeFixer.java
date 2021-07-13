/*-
 * Copyright (c) 2021 Marián Konček
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mkoncek.classpathless;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

public class SourceCodeFixer {
    public static void main(String args[]) throws Exception {
        ASTParser parser = ASTParser.newParser(AST.JLS_Latest);

        final String source = "import java.util..Set; // comment\n public static final class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }";
        parser.setSource(source.toCharArray());
        //parser.setSource("/*abc*/".toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        //ASTNode node = parser.createAST(null);

        var document = new Document(source);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        cu.recordModifications();

        // TextEdit

        var edit = new MultiTextEdit();
        int offset = -1;

        cu.accept(new ASTVisitor() {

            Set names = new HashSet();

            @Override
            public boolean visit(ImportDeclaration node) {
                System.out.println(node.toString());
                return false;
            }

            @Override
            public boolean visit(TypeDeclaration node) {
                if (!node.isInterface()) {
                    edit.addChild(new InsertEdit(node.getStartPosition(), "abstract "));
                }
                System.out.println("typedecl " + node.getStartPosition());
                System.out.println(node.toString());

                // node.modifiers().add(Modifier.ABSTRACT);
                for (var f : node.modifiers()) {
                    var m = (Modifier) f;
                    System.out.print("HERE ");
                    System.out.println(m.toString());
                    System.out.println(m.getClass().toString());
                }
                return false;
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

        edit.apply(document);

        System.out.println(document.get());
    }
}
