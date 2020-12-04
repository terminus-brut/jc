package org.jc.api;


import java.util.Collection;

public interface InMemoryCompiler {

    /**
     * @param classprovider provider for missing elements on classapth
     * @param javaSourceFiles files to compile
     * @return compile dbytecode of all javaSourceFiles
     */
    Collection<IdentifiedBytecode> compileClass(ClassesProvider classprovider, IdentifiedSource... javaSourceFiles);
    
}
