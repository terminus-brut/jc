package org.jc.api;

/**
 * Compiled bytecode of class with its fully qualified name
 */
public class IdentifiedBytecode extends IdentifiedFile {

    /**
     *
     * @param classIdentifier fully qualified name of class
     * @param file bytecode content of the file
     */
    public IdentifiedBytecode(ClassIdentifier classIdentifier, byte[] file) {
        super(classIdentifier, file);
    }
}


