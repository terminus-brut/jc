/*-
 * Copyright (c) 2021 Marián Konček
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        var obj = new InMemoryJavaClassFileObject("Hello.class", new NullClassesProvider());
        assertEquals("class:///Hello.class", obj.toUri().toASCIIString());
    }

    @Test
    void testContent() {
        var obj = new InMemoryJavaClassFileObject("Hello.class", new NullClassesProvider());
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.class")) {
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
