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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
public class CompilerJavac implements InMemoryCompiler {
    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private InMemoryFileManager fileManager;
    private Arguments arguments;

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
                        "Compiler diagnostic at {5}[{0}, {1}]: {2}{3}(code: {4})",
                        diagnostic.getLineNumber(), diagnostic.getColumnNumber(), msg,
                        System.lineSeparator(), diagnostic.getCode(),
                        (diagnostic.getSource() != null ? "(" + diagnostic.getSource().getName() + ") " : " ")));
            }

            if (diagnostic.getCode().equals("compiler.err.does.not.override.abstract")) {
                /// TODO try to make the class abstract and recompile
            }
        }
    }

    public CompilerJavac(Arguments arguments) {
        this.fileManager = new InMemoryFileManager(
                compiler.getStandardFileManager(null, null, null));
        this.arguments = arguments;
    }

    public CompilerJavac() {
        this(new Arguments());
    }

    @Override
    public Collection<IdentifiedBytecode> compileClass(
            ClassesProvider classprovider,
            Optional<MessagesListener> messagesConsumer,
            IdentifiedSource... javaSourceFiles) {
        final List<JavaFileObject> compilationUnits = new ArrayList<>();

        for (var source : Arrays.asList(javaSourceFiles)) {
            compilationUnits.add(new InMemoryJavaSourceFileObject(source));
        }

        var messagesListener = messagesConsumer.orElse(new NullMessagesListener());

        fileManager.setClassProvider(classprovider);

        var availableClasses = new TreeSet<>(classprovider.getClassPathListing());

        fileManager.setAvailableClasses(availableClasses);
        fileManager.setLoggingSwitch(new LoggingSwitch());
        fileManager.setArguments(arguments);

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
        var classOutputs = new ArrayList<JavaFileObject>();

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
