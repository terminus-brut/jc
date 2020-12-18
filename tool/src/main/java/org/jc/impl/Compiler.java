/*-
 * Copyright (c) 2020 Marián Konček
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
package org.jc.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class Compiler {
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final InMemoryFileManager file_manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));
    private DiagnosticListener<JavaFileObject> diagnostics = new DiagnosticListener<>() {
        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            var msg = diagnostic.getMessage(Locale.ENGLISH);
            final var begin_text = "package ";
            
            System.err.println("DiagnosticListener reporting: " + msg + ", " + diagnostic.getCode());
            System.err.println("DiagnosticListener: \"" + diagnostic.getCode() + "\"");
            
            if (diagnostic.getCode().equals("compiler.err.doesnt.exist")) {
                if (msg.startsWith(begin_text)) {
                    final var begin_idx = begin_text.length();
                    final var end_idx = msg.indexOf(" does not exist");
                    
                    missing_dependencies.add(msg.substring(begin_idx, end_idx));
                }
            }
        }
    };
    private List<String> missing_dependencies = new ArrayList<>();
    final List<JavaFileObject> compilation_units = new ArrayList<>();

    public Compiler() throws IOException {
        /*
        System.out.println(tempdir.toString());
        file_manager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(tempdir.toFile()));
                */
    }

    public void add(JavaFileObject object) {
        compilation_units.add(object);
    }

    public void compile(Path output_directory) throws IOException {
        while (!compiler.getTask(
                null,
                file_manager,
                diagnostics,
                null,
                null,
                compilation_units)
                .call()) {
            if (!missing_dependencies.isEmpty()) {
                System.err.println("resolving deps: " + missing_dependencies.stream().collect(Collectors.joining(", ")));
                break;
            } else {
                throw new RuntimeException("Could not compile file");
            }
        }
        
        for (final var class_out : file_manager.classOutputs()) {
            var out_path = output_directory.resolve(
                    Paths.get("./" + class_out.getName() + ".class"));
            
            System.out.println(out_path);
            
            try (var os = new FileOutputStream(out_path.toFile())) {
                class_out.openInputStream().transferTo(os);
            }
        }
    }
}
