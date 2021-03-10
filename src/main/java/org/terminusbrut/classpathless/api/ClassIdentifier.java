package org.terminusbrut.classpathless.api;

import java.util.Objects;

/**
 * Wrapper around fully qualified class name.
 * Maybe it is idiotic idea, but I have some bad feelings
 * about handling of compiled inner classes.
 * Where `$` is just start of that bad feeling.
 */
public class ClassIdentifier {
    private final String fullName;
    
    public ClassIdentifier(String fullName) {
        this.fullName = fullName;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassIdentifier that = (ClassIdentifier) o;
        return Objects.equals(fullName, that.fullName);
    }
    
    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
    
    @Override
    public String toString() {
        return fullName;
    }
}
