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
package io.github.mkoncek.classpathless.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.ClassesProvider;
import io.github.mkoncek.classpathless.api.IdentifiedBytecode;
import io.github.mkoncek.classpathless.api.IdentifiedSource;
import io.github.mkoncek.classpathless.api.InMemoryCompiler;
import io.github.mkoncek.classpathless.api.MessagesListener;

/**
 * An implementation using Eclipse JDT compiler API
 */
public class CompilerECJ implements InMemoryCompiler {
    private LoggingSwitch loggingSwitch;

    private static class TempDirectory implements AutoCloseable {
        private final Path directory;

        public TempDirectory(Path directory) {
            if (!directory.toFile().isDirectory()) {
                throw new RuntimeException("Path is not a directory");
            }
            this.directory = directory;
        }

        @Override
        public void close() throws Exception {
            /*
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
             */
        }
    }

    public CompilerECJ() {
        this.loggingSwitch = new LoggingSwitch();
    }

    private void pullClasses(SortedSet<String> availableClasses, String importName,
            ClassesProvider classprovider, Path tmpDirectory) throws IOException {
        for (var availableClassName : availableClasses.tailSet(importName)) {
            if (!availableClassName.startsWith(importName)) {
                break;
            }

            if (availableClassName.length() > importName.length()
                    && availableClassName.charAt(importName.length()) != '.') {
                break;
            }

            if (availableClassName.contains("$$Lambda$")) {
                loggingSwitch.logln(Level.FINE, "Ignoring lambda class \"{0}\"", availableClassName);
                continue;
            }

            loggingSwitch.logln(Level.FINE, "Pulling class from ClassProvider: \"{0}\"", availableClassName);
            var found = classprovider.getClass(new ClassIdentifier(availableClassName));
            if (!found.isEmpty()) {
                for (var identified : found) {
                    Path resolved = tmpDirectory.resolve("classes").resolve(
                            identified.getClassIdentifier().getFullName().replace('.', '/') + ".class");
                    var parent = resolved.getParent();
                    if (parent == null) {
                        throw new IllegalStateException();
                    }
                    Files.createDirectories(parent);
                    try (var os = new FileOutputStream(resolved.toFile()))
                    {
                        os.write(identified.getFile());
                    }
                }
            }
        }
    }

    @Override
    public Collection<IdentifiedBytecode> compileClass(
            ClassesProvider classprovider,
            Optional<MessagesListener> messagesConsummer,
            IdentifiedSource... javaSourceFiles) {
        var result = new ArrayList<IdentifiedBytecode>(javaSourceFiles.length);

        var classes = new TreeSet<>(classprovider.getClassPathListing());

        try (var tmpdir = new TempDirectory(Files.createTempDirectory("cplc-ecj-"))) {
            loggingSwitch.logln(Level.INFO, "Created temporary directory {0}", tmpdir.directory);

            for (var source : javaSourceFiles) {
                {
                    Path resolved = tmpdir.directory.resolve("sources").resolve(
                            source.getClassIdentifier().getFullName().replace('.', '/') + ".java");
                    var parent = resolved.getParent();
                    if (parent == null) {
                        throw new IllegalStateException();
                    }
                    Files.createDirectories(parent);
                    try (var os = new FileOutputStream(resolved.toFile()))
                    {
                        os.write(source.getFile());
                    }
                }

                String packageName = JavaSourceReader.readSourcePackage(source.getSourceCode());

                if (packageName != null) {
                    pullClasses(classes, packageName, classprovider, tmpdir.directory);
                }

                for (var importName : JavaSourceReader.readImports(source.getSourceCode())) {
                    if (importName.endsWith(".*")) {
                        importName = importName.substring(0, importName.length() - 2);
                    }

                    pullClasses(classes, importName, classprovider, tmpdir.directory);
                }
            }

            Files.createDirectory(tmpdir.directory.resolve("result"));

            var ecjArgs = new String[] {
                    /// Workaround for error "Unable to load annotation processing manager
                    /// org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager
                    /// from classpath."
                    ///
                    /// See: https://www.eclipse.org/forums/index.php/t/1082352/
                    "-proc:none",

                    "-source", "8",

                    tmpdir.directory.resolve("sources").toString(),

                    "-classpath",
                    tmpdir.directory.resolve("classes").toString(),

                    "-d",
                    tmpdir.directory.resolve("result").toString(),
            };

            var byteStream = new ByteArrayOutputStream();

            boolean compilationResult = BatchCompiler.compile(ecjArgs,
                    new PrintWriter(byteStream, true, StandardCharsets.UTF_8),
                    new PrintWriter(byteStream, true, StandardCharsets.UTF_8),
                    null);

            if (messagesConsummer.isPresent()) {
                messagesConsummer.get().addMessage(Level.SEVERE,
                        new String(byteStream.toByteArray(), StandardCharsets.UTF_8));
            }

            if (!compilationResult) {
                throw new RuntimeException("Could not compile file");
            }

            Path resultDir = tmpdir.directory.resolve("result");
            Files.walkFileTree(resultDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    byte[] content = Files.readAllBytes(file);

                    String identifier = resultDir.relativize(file).toString();
                    /// Remove ".class"
                    identifier = identifier.substring(0, identifier.length() - 6);
                    identifier = identifier.replace('/', '.');

                    result.add(new IdentifiedBytecode(new ClassIdentifier(identifier), content));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            loggingSwitch.logln(Level.INFO, "Removed temporary directory");
        }

        return result;
    }

    public static void main(String[] args) {
        var compiler = new CompilerECJ();
        compiler.compileClass(new NullClassesProvider(), Optional.empty());
    }
}
