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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.IdentifiedSource;
import io.github.mkoncek.classpathless.api.MessagesListener;

public class CompilerJavacTest {
    private JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private StandardJavaFileManager fm = javac.getStandardFileManager(null, null, StandardCharsets.UTF_8);
    private static final Optional<MessagesListener> printingListener = Optional.of(new PrintingMessagesListener());
    private static final Comparator<byte[]> byteArrayComparator = (byte[] lhs, byte[] rhs) -> Arrays.compare(lhs, rhs);

    private static void setProperties() {
        // System.setProperty("io.github.mkoncek.cplc.logging", "");
        // System.setProperty("io.github.mkoncek.cplc.loglevel", "finest");
        // System.setProperty("io.github.mkoncek.cplc.tracing", "");
    }

    @Test
    public void testSimple() throws Exception {
        setProperties();

        var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

        var classes = new String[] {
                "src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.class",
        };

        for (var cf : classes) {
            Files.deleteIfExists(Paths.get(cf));
        }

        javac.getTask(null, fm, null, null, null, fm.getJavaFileObjects(
                "src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.java")).call();

        for (var cf : classes) {
            try (var is = new FileInputStream(cf)) {
                expectedBytecodeContents.add(is.readAllBytes());
            }
        }

        byte[] content;
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.java")) {
            content = is.readAllBytes();
        }

        var jc = new CompilerJavac();
        var source = new IdentifiedSource(new ClassIdentifier("Hello"), content);

        var compilationResult = jc.compileClass(new NullClassesProvider(), printingListener, source);
        assertEquals(expectedBytecodeContents.size(), compilationResult.size());

        var compiledBytecodes = new TreeSet<>(byteArrayComparator);
        for (var ib : compilationResult) {
            compiledBytecodes.add(ib.getFile());
        }

        var actit = compiledBytecodes.iterator();
        var expit = expectedBytecodeContents.iterator();

        while (expit.hasNext()) {
            assertArrayEquals(expit.next(), actit.next());
        }
        assertEquals(false, actit.hasNext());
    }

    @Test
    public void testNested() throws Exception {
        setProperties();

        var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

        var classes = new String[] {
                "src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello.class",
                "src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello$Inner.class",
        };

        for (var cf : classes) {
            Files.deleteIfExists(Paths.get(cf));
        }

        javac.getTask(null, fm, null, null, null, fm.getJavaFileObjects(
                "src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello.java")).call();

        for (var cf : classes) {
            try (var is = new FileInputStream(cf)) {
                expectedBytecodeContents.add(is.readAllBytes());
            }
        }

        byte[] content;
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello.java")) {
            content = is.readAllBytes();
        }

        var jc = new CompilerJavac();
        var source = new IdentifiedSource(new ClassIdentifier("Hello"), content);
        var compilationResult = jc.compileClass(new NullClassesProvider(), printingListener, source);
        assertEquals(expectedBytecodeContents.size(), compilationResult.size());

        var compiledBytecodes = new TreeSet<>(byteArrayComparator);
        for (var ib : compilationResult) {
            compiledBytecodes.add(ib.getFile());
        }

        var actit = compiledBytecodes.iterator();
        var expit = expectedBytecodeContents.iterator();

        while (expit.hasNext()) {
            assertArrayEquals(expit.next(), actit.next());
        }
        assertEquals(false, actit.hasNext());
    }

    @Test
    public void testAnonymous() throws Exception {
        setProperties();

        var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

        var classes = new String[] {
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.class",
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$1.class",
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$InnerInterface.class",
        };

        for (var cf : classes) {
            Files.deleteIfExists(Paths.get(cf));
        }

        javac.getTask(null, fm, null, null, null, fm.getJavaFileObjects(
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.java")).call();

        for (var cf : classes) {
            try (var is = new FileInputStream(cf)) {
                expectedBytecodeContents.add(is.readAllBytes());
            }
        }

        byte[] content;
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.java")) {
            content = is.readAllBytes();
        }

        var jc = new CompilerJavac();
        var source = new IdentifiedSource(new ClassIdentifier("Hello"), content);
        var compilationResult = jc.compileClass(new NullClassesProvider(), printingListener, source);
        assertEquals(expectedBytecodeContents.size(), compilationResult.size());

        var compiledBytecodes = new TreeSet<>(byteArrayComparator);
        for (var ib : compilationResult) {
            compiledBytecodes.add(ib.getFile());
        }

        var actit = compiledBytecodes.iterator();
        var expit = expectedBytecodeContents.iterator();

        while (expit.hasNext()) {
            assertArrayEquals(expit.next(), actit.next());
        }
        assertEquals(false, actit.hasNext());
    }

    @Test
    public void testRepeated() throws Exception {
        setProperties();

        var jc = new CompilerJavac();

        for (int i = 0; i != 3; ++i) {
            var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

            var classes = new String[] {
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.class",
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$1.class",
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$InnerInterface.class",
            };

            for (var cf : classes) {
                Files.deleteIfExists(Paths.get(cf));
            }

            javac.getTask(null, fm, null, null, null, fm.getJavaFileObjects(
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.java")).call();

            for (var cf : classes) {
                try (var is = new FileInputStream(cf)) {
                    expectedBytecodeContents.add(is.readAllBytes());
                }
            }

            byte[] content;
            try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.java")) {
                content = is.readAllBytes();
            }

            var source = new IdentifiedSource(new ClassIdentifier("Hello"), content);
            var compilationResult = jc.compileClass(new NullClassesProvider(), printingListener, source);
            assertEquals(expectedBytecodeContents.size(), compilationResult.size());

            var compiledBytecodes = new TreeSet<>(byteArrayComparator);
            for (var ib : compilationResult) {
                compiledBytecodes.add(ib.getFile());
            }

            var actit = compiledBytecodes.iterator();
            var expit = expectedBytecodeContents.iterator();

            while (expit.hasNext()) {
                assertArrayEquals(expit.next(), actit.next());
            }
            assertEquals(false, actit.hasNext());
        }
    }
}
