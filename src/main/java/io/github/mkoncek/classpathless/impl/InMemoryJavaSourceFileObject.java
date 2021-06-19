/*-
 * Copyright (c) 2020 Marián Konček
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.tools.SimpleJavaFileObject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author Marián Konček
 */
public class InMemoryJavaSourceFileObject extends SimpleJavaFileObject {
    String source;

    protected InMemoryJavaSourceFileObject(String name) {
        super(URI.create("string:///" + name), Kind.SOURCE);
    }

    InMemoryJavaSourceFileObject(String name, String source) {
        this(name);
        this.source = source;
    }

    InMemoryJavaSourceFileObject(String name, InputStream is) throws IOException {
        this(name);
        var byteStream = new ByteArrayOutputStream();
        is.transferTo(byteStream);
        this.source = byteStream.toString(StandardCharsets.US_ASCII);
    }

    @SuppressFBWarnings(value = {"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification = "can not see it:(")
    public InMemoryJavaSourceFileObject(Path path) throws IOException {
        this(path.getFileName().toString(), new FileInputStream(path.toFile()));
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return source;
    }
}
