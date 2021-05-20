package org.terminusbrut.classpathless.api;


import java.util.Collection;
import java.util.Optional;

public interface InMemoryCompiler {
    /**
     * @param classprovider provider for missing elements on classpath
     * @param javaSourceFiles files to compile
     * @return compiled bytecode of all javaSourceFiles
     */
    Collection<IdentifiedBytecode> compileClass(ClassesProvider classprovider,
            Optional<MessagesListener> messagesConsummer, IdentifiedSource... javaSourceFiles);
}
