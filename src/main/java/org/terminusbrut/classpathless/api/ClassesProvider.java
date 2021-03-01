package org.terminusbrut.classpathless.api;


import java.util.Collection;
import java.util.List;

public interface ClassesProvider {
    /**
     * Callback for compiler, which provides, on demand, the depnendencies compiler is missing.
     *
     * @param names names of classes the provider should return
     * @return bytecode of all found classes
     */
    Collection<IdentifiedBytecode> getClass(ClassIdentifier... names);

    /**
     * Warning, may include labdas and will include inner classes with $notations
     * Intentionally not usinf ClassIdentifier, but may chnage to it
     *
     * @return all fully qualified classes visible from classapth
     */
    List<String> getClassPathListing();
}
