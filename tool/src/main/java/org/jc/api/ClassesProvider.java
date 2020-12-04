package org.jc.api;


import java.util.Collection;

public interface ClassesProvider {

    /**
     * Callback for compiler, which provides, on demand, the depnendencies compiler is missing.
     *
     * @param names names of classes the provider should return
     * @return bytecode of all found classes
     */
    Collection<IdentifiedBytecode> getClass(ClassIdentifier... names);

}
