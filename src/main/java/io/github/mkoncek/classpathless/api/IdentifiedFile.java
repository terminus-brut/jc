package io.github.mkoncek.classpathless.api;

import java.util.Objects;
import edu.umd.cs.findbugs.annotations.*;

public class IdentifiedFile {
    private final ClassIdentifier classIdentifier;
    private final byte[] file;

    @SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "pure wrapper class")
    public IdentifiedFile(ClassIdentifier classIdentifier, byte[] file) {
        this.classIdentifier = classIdentifier;
        this.file = file;
    }

    public ClassIdentifier getClassIdentifier() {
        return classIdentifier;
    }
    
    @SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "pure wrapper class")
    public byte[] getFile() {
        return file;
    }

    @Override
    public String toString() {
        return classIdentifier + "*" + file.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiedFile that = (IdentifiedFile) o;
        return Objects.equals(classIdentifier, that.classIdentifier);
    }

    @Override
    public int hashCode() {
        return classIdentifier.hashCode();
    }
}
