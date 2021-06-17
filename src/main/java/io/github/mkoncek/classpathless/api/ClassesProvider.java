package io.github.mkoncek.classpathless.api;

import java.util.Collection;
import java.util.List;

public interface ClassesProvider {
    /**
     * Callback for compiler, which provides, on demand, the dependencies compiler is missing.
     *
     * @param names names of classes the provider should return
     * @return bytecode of all found classes
     */
    Collection<IdentifiedBytecode> getClass(ClassIdentifier... names);

    /**
     * Warning, may include lambdas and will include inner classes with $notations.
     * Intentionally not using ClassIdentifier, but may change to it
     *
     * @return all fully qualified classes visible from classpath
     */
    List<String> getClassPathListing();
}
