package io.github.mkoncek.classpathless.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

public class InMemoryJavaSourceFileObjectTest {
    @Test
    void testConstructor() {
        var obj = new InMemoryJavaSourceFileObject("Hello.java");
        assertEquals("string:///Hello.java", obj.toUri().toASCIIString());
    }

    @Test
    void testContentSimple() {
        final var content = "" +
                "public class Hello {" +
                "    public static void main(String[] args) {" +
                "        System.out.println(\"Hello World!\");" +
                "    }" +
                "}";

        var obj = new InMemoryJavaSourceFileObject("Hello.java", content);

        try {
            assertEquals(content, obj.getCharContent(true));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Test
    void testContentFromFile() {
        var contentBuilder = new StringBuilder();

        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.java")) {
            var reader = new BufferedReader(new InputStreamReader(is));
            while (reader.ready()) {
                contentBuilder.append(reader.readLine());
                contentBuilder.append(System.lineSeparator());
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.java")) {
            var obj = new InMemoryJavaSourceFileObject("Hello.java", is);
            var content = contentBuilder.toString();
            assertEquals(content, obj.getCharContent(true));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
