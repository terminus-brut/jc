package org.terminusbrut.classpathless.api;


import java.util.Collection;
import java.util.Optional;

public interface InMemoryCompiler {
    /**
     * @param classprovider Provider for missing elements on the classpath.
     * @param messagesConsummer Accepts any diagnostic or logging information
     * from the compiler.
     * @param javaSourceFiles Files to compile.
     * @return Compiled bytecode of all javaSourceFiles.
     */
    Collection<IdentifiedBytecode> compileClass(ClassesProvider classprovider,
            Optional<MessagesListener> messagesConsummer, IdentifiedSource... javaSourceFiles);
}
