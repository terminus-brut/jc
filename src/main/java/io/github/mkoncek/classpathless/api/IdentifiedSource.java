package io.github.mkoncek.classpathless.api;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Java source file with its fully qualified name.
 */
@SuppressFBWarnings(value = {"EQ_DOESNT_OVERRIDE_EQUALS"}, justification = "intentionally using equals from superclass")
public class IdentifiedSource extends IdentifiedFile {
    private final Charset charset;

    /**
     * @param classIdentifier fully qualified name of class.
     * @param file java source code of class.
     * @param charset charset of source which was used to get the byte[].
     * Default encoding is used if not provided.
     */
    public IdentifiedSource(ClassIdentifier classIdentifier, byte[] file, Charset charset) {
        super(classIdentifier, file);
        this.charset = charset;
    }

    public IdentifiedSource(ClassIdentifier classIdentifier, byte[] file) {
        this(classIdentifier, file, StandardCharsets.UTF_8);
    }

    public String getSourceCode() {
        return new String(getFile(), charset);
    }
}
