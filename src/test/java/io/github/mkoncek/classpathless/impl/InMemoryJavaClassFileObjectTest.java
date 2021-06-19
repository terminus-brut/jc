package io.github.mkoncek.classpathless.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

public class InMemoryJavaClassFileObjectTest {
    @Test
    void testConstructor() {
        var obj = new InMemoryJavaClassFileObject("Hello.class");
        assertEquals("class:///Hello.class", obj.toUri().toASCIIString());
    }

    @Test
    void testContent() {
        var obj = new InMemoryJavaClassFileObject("Hello.class");
        try (var is = new FileInputStream("src/test/resources/Hello.class")) {
            var fileContent = is.readAllBytes();
            obj.openOutputStream().write(fileContent);

            var objContent = obj.openInputStream().readAllBytes();
            assertArrayEquals(fileContent, objContent);

            objContent = obj.openInputStream().readAllBytes();
            assertArrayEquals(fileContent, objContent);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
