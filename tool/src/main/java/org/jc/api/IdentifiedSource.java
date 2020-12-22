package org.jc.api;


import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Java source file with its fully qualified name.
 */
public class IdentifiedSource extends IdentifiedFile {
    private final Optional<String> encoding;
    
    /**
     *
     * @param classIdentifier fully qualified name of class
     * @param file java source code of class
     * @param encoding optional encoding of source which was used to get the byte[]. Default encoding is used if not provided
     */
    public IdentifiedSource(ClassIdentifier classIdentifier, byte[] file, Optional<String> encoding) {
        super(classIdentifier, file);
        this.encoding = encoding;
    }

    public String getSourceCode() throws UnsupportedEncodingException {
        if (encoding.isPresent()) {
            return new String(getFile(), encoding.get());
        } else {
            return new String(getFile());
        }
    }
}


