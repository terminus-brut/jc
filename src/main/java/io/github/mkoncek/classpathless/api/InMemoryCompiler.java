package io.github.mkoncek.classpathless.api;


import java.util.Collection;
import java.util.Optional;

public interface InMemoryCompiler {
    static class Arguments {
        private boolean useHostJavaClasses = true;

        public boolean useHostJavaClasses() {
            return useHostJavaClasses;
        }

        public Arguments useHostJavaClasses(boolean value) {
            useHostJavaClasses = value;
            return this;
        }
    }

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
