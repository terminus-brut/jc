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
package org.terminusbrut.classpathless;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.terminusbrut.classpathless.api.ClassIdentifier;
import org.terminusbrut.classpathless.api.IdentifiedSource;
import org.terminusbrut.classpathless.impl.Compiler;
import org.terminusbrut.classpathless.impl.InMemoryJavaSourceFileObject;
import org.terminusbrut.classpathless.impl.JavaSourcePackageNameReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Marián Konček
 */
public class Tool {
    static class Arguments {
        @Parameter(names = {"-h", "--help"}, help = true,
                description = "Display help.")
        boolean help = false;

        @Parameter(names = {"-cp", "-classpath"}, description = "Classpath")
        String classpath = null;

        @Parameter(description = "Input files", required = true, variableArity = true)
        List<String> inputs = new ArrayList<>();

        @Parameter(names = {"-d"}, description = "Output directory")
        String output = ".";
    }

    public static void main(String[] args) throws IOException {
        var arguments = new Arguments();
        var jcommander = JCommander.newBuilder().addObject(arguments).build();
        jcommander.parse(args);

        if (arguments.help) {
            jcommander.usage();
            return;
        }

        var ccp = new ClasspathClassesProvider(arguments.classpath);

        var compiler = new Compiler();

        var sources = new IdentifiedSource[arguments.inputs.size()];

        for (int i = 0; i != arguments.inputs.size(); ++i) {
            final var inputFile = arguments.inputs.get(i);

            var sourceObject = new InMemoryJavaSourceFileObject(Paths.get(inputFile));

            String fullyQualifiedName;
            try (var fis = new FileInputStream(inputFile)) {
                fullyQualifiedName = new JavaSourcePackageNameReader(fis).readSourcePackage();
            }

            {
                var classPath = Paths.get(inputFile).getFileName();
                if (classPath != null) {
                    var className = classPath.toString();
                    /// Remove ".java"
                    className = className.substring(0, className.length() - 5);
                    if (fullyQualifiedName == null) {
                        fullyQualifiedName = className;
                    } else {
                        fullyQualifiedName += "." + className;
                    }
                } else {
                    throw new IllegalStateException();
                }
            }

            var sourceIdentifier = new ClassIdentifier(fullyQualifiedName);

            var content = sourceObject.getCharContent(true).toString().getBytes(StandardCharsets.UTF_8);

            sources[i] = new IdentifiedSource(sourceIdentifier, content, Optional.empty());
        }

        for (var result : compiler.compileClass(ccp, Optional.empty(), sources)) {
            var outPath = Paths.get(arguments.output).resolve(
                    Paths.get("./" + result.getClassIdentifier().getFullName()
                            .replace(".", File.separator) + ".class"));

            var parent = outPath.getParent();
            if (parent == null) {
                throw new IllegalStateException();
            }

            Files.createDirectories(parent);

            try (var os = new FileOutputStream(outPath.toFile())) {
                os.write(result.getFile());
            }
        }
    }
}
