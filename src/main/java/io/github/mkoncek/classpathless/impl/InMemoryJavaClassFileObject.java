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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.tools.SimpleJavaFileObject;

/**
 * @author Marián Konček
 */
public class InMemoryJavaClassFileObject extends SimpleJavaFileObject {
    private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    public InMemoryJavaClassFileObject(String name) {
        super(URI.create("class:///" + name), Kind.CLASS);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return byteStream.toString(StandardCharsets.US_ASCII);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(byteStream.toByteArray());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return byteStream;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        return super.isNameCompatible(simpleName, kind);
    }
}
