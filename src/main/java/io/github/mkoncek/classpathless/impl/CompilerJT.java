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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.ClassesProvider;
import io.github.mkoncek.classpathless.api.IdentifiedBytecode;
import io.github.mkoncek.classpathless.api.IdentifiedSource;
import io.github.mkoncek.classpathless.api.InMemoryCompiler;
import io.github.mkoncek.classpathless.api.MessagesListener;

/**
 * An implementation using javax.tools compiler API
 */
public class CompilerJT implements InMemoryCompiler {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    InMemoryFileManager fileManager;

    static private class DiagnosticToMessagesListener implements DiagnosticListener<JavaFileObject> {
        MessagesListener listener;

        DiagnosticToMessagesListener(MessagesListener listener) {
            this.listener = listener;
        }

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            var msg = diagnostic.getMessage(Locale.ENGLISH);
            if (listener != null) {
                listener.addMessage(Level.SEVERE, MessageFormat.format(
                        "Compiler diagnostic at ({5}) [{0}, {1}]: {2}{3}(code: {4})",
                        diagnostic.getLineNumber(), diagnostic.getColumnNumber(), msg,
                        System.lineSeparator(), diagnostic.getCode(), diagnostic.getSource().getName()));
            }

            if (diagnostic.getCode().equals("compiler.err.does.not.override.abstract")) {
                /// TODO try to make the class abstract and recompile
            }
        }
    }

    final List<JavaFileObject> compilationUnits = new ArrayList<>();

    public CompilerJT() throws IOException {
        System.out.println("new compiler");
        this.fileManager = new InMemoryFileManager(
                compiler.getStandardFileManager(null, null, null));
    }

    @Override
    public Collection<IdentifiedBytecode> compileClass(
            ClassesProvider classprovider,
            Optional<MessagesListener> messagesConsumer,
            IdentifiedSource... javaSourceFiles) {
        final List<JavaFileObject> compilationUnits = new ArrayList<>();

        System.out.println("compile class");

        try {
            for (var source : javaSourceFiles) {
                /// NOTE the JavaFileObject's toUri method needs to return something
                /// that ends with the proper Java source file name and extension
                /// otherwise the compiler throws an error
                var sourceName = source.getClassIdentifier().getFullName();
                sourceName = sourceName.replace(".", "/") + ".java";

                compilationUnits.add(new InMemoryJavaSourceFileObject(
                        sourceName, source.getSourceCode()));
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        var messagesListener = messagesConsumer.orElse(new LoggingSwitch.NullMessagesListener());
        PrintStream loggerPrinter;

        {
            var logging = System.getProperty("io.github.mkoncek.cplc.logging");
            if (logging == null) {
                loggerPrinter = new PrintStream(PrintStream.nullOutputStream(), false, StandardCharsets.UTF_8);
            } else {
                if (logging.isEmpty()) {
                    loggerPrinter = System.err;
                } else {
                    FileOutputStream os;
                    try {
                        /// TODO not closed if any exception happens
                        os = new FileOutputStream(Paths.get(logging).toFile());
                        loggerPrinter = new PrintStream(os, true, StandardCharsets.UTF_8);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }
            }
        }

        var loggingSwitch = new LoggingSwitch(loggerPrinter, messagesListener);

        {
            var level = Level.OFF;
            var loglevel = System.getProperty("io.github.mkoncek.cplc.loglevel");
            if (loglevel != null) {
                switch (loglevel) {
                    case "all":
                        level = Level.ALL;
                        break;

                    case "finest":
                        level = Level.FINEST;
                        break;

                    case "finer":
                        level = Level.FINER;
                        break;

                    case "fine":
                        level = Level.FINE;
                        break;

                    case "config":
                        level = Level.CONFIG;
                        break;

                    case "info":
                        level = Level.INFO;
                        break;

                    case "warning":
                        level = Level.WARNING;
                        break;

                    case "severe":
                        level = Level.SEVERE;
                        break;

                    case "off":
                        level = Level.OFF;
                        break;

                    default :
                        throw new IllegalArgumentException("Unrecognized logging level: \"" + loglevel + "\"");
                }
            }

            loggingSwitch.setLogLevel(level);
        }

        if (System.getProperty("io.github.mkoncek.cplc.tracing") != null) {
            loggingSwitch.setTracing(true);
        }

        var classOutputs = new ArrayList<JavaFileObject>();
        fileManager.setClassProvider(classprovider);

        var availableClasses = new TreeSet<>(classprovider.getClassPathListing());

        fileManager.setAvailableClasses(availableClasses);

        loggerPrinter.println(availableClasses);

        fileManager.setLoggingSwitch(loggingSwitch);

        if (!compiler.getTask(
                null,
                fileManager,
                new DiagnosticToMessagesListener(messagesListener),
                null,
                null,
                compilationUnits)
                .call()) {
            throw new RuntimeException("Could not compile file");
        }

        var result = new ArrayList<IdentifiedBytecode>();

        fileManager.clearAndGetOutput(classOutputs);

        for (final var classOutput : classOutputs) {
            /// Remove the leading "/"
            var identifier = new ClassIdentifier(classOutput.getName().substring(1));
            byte[] content;
            try {
                content = classOutput.openInputStream().readAllBytes();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
            result.add(new IdentifiedBytecode(identifier, content));
        }

        fileManager.setClassProvider(null);
        fileManager.setLoggingSwitch(null);

        return result;
    }
}
