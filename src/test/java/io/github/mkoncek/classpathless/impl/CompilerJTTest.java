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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.ClassesProvider;
import io.github.mkoncek.classpathless.api.IdentifiedBytecode;
import io.github.mkoncek.classpathless.api.IdentifiedSource;
import io.github.mkoncek.classpathless.api.MessagesListener;

public class CompilerJTTest {
    static class EmptyClassesProvider implements ClassesProvider {
        @Override
        public List<String> getClassPathListing() {
            return Collections.emptyList();
        }

        @Override
        public Collection<IdentifiedBytecode> getClass(ClassIdentifier... names) {
            return Collections.emptyList();
        }
    }

    private static final ClassesProvider emptyClassesProvider = new EmptyClassesProvider();
    private static final Optional<MessagesListener> printingListener = Optional.of(new PrintingMessagesListener());
    private static final Comparator<byte[]> byteArrayComparator = (byte[] lhs, byte[] rhs) -> Arrays.compare(lhs, rhs);

    @Test
    public void testSimple() throws Exception {
        var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

        for (var name : new String[] {
                "src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.class",
        }) {
            try (var is = new FileInputStream(name))
            {
                expectedBytecodeContents.add(is.readAllBytes());
            }
        }

        byte[] content;
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/SimpleHello/Hello.java")) {
            content = is.readAllBytes();
        }

        var jc = new CompilerJT();
        var source = new IdentifiedSource(new ClassIdentifier("Hello"), content, Optional.empty());
        var compilationResult = jc.compileClass(emptyClassesProvider, printingListener, source);
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
        var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

        for (var name : new String[] {
                "src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello.class",
                "src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello$Inner.class",
        }) {
            try (var is = new FileInputStream(name))
            {
                expectedBytecodeContents.add(is.readAllBytes());
            }
        }

        byte[] content;
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/NestedHello/Hello.java")) {
            content = is.readAllBytes();
        }

        var jc = new CompilerJT();
        var source = new IdentifiedSource(new ClassIdentifier("Hello"), content, Optional.empty());
        var compilationResult = jc.compileClass(emptyClassesProvider, printingListener, source);
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
        var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

        for (var name : new String[] {
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.class",
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$1.class",
                "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$InnerInterface.class",
        }) {
            try (var is = new FileInputStream(name))
            {
                expectedBytecodeContents.add(is.readAllBytes());
            }
        }

        byte[] content;
        try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.java")) {
            content = is.readAllBytes();
        }

        var jc = new CompilerJT();
        var source = new IdentifiedSource(new ClassIdentifier("Hello"), content, Optional.empty());
        var compilationResult = jc.compileClass(emptyClassesProvider, printingListener, source);
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
        var jc = new CompilerJT();

        for (int i = 0; i != 3; ++i) {
            var expectedBytecodeContents = new TreeSet<byte[]>(byteArrayComparator);

            for (var name : new String[] {
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.class",
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$1.class",
                    "src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello$InnerInterface.class",
            }) {
                try (var is = new FileInputStream(name))
                {
                    expectedBytecodeContents.add(is.readAllBytes());
                }
            }

            byte[] content;
            try (var is = new FileInputStream("src/test/resources/io/github/mkoncek/classpathless/impl/AnonymousHello/Hello.java")) {
                content = is.readAllBytes();
            }

            var source = new IdentifiedSource(new ClassIdentifier("Hello"), content, Optional.empty());
            var compilationResult = jc.compileClass(emptyClassesProvider, printingListener, source);
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
