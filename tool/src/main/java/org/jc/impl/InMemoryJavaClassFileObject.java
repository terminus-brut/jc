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
package org.jc.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.tools.SimpleJavaFileObject;

import org.apache.commons.io.output.AbstractByteArrayOutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;

/**
 * @author Marián Konček
 */
public class InMemoryJavaClassFileObject extends SimpleJavaFileObject {
    private AbstractByteArrayOutputStream bytes = new UnsynchronizedByteArrayOutputStream();
    // private Set<JavaFileObject> objects;
    
    // public InMemoryJavaClassFileObject(/* JavaFileObject fileObject, Set<JavaFileObject> objects*/) {
    public InMemoryJavaClassFileObject(String name) {
        // super(fileObject);
        // this.objects = objects;
        // new SimpleJavaFileObject(URI.create("class:///" + name), Kind.CLASS);
        super(URI.create("class:///" + name), Kind.CLASS);
    }
    
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        System.err.println(DebugPrinter.toString("InMemoryJavaClassFileObject::getCharContent", ignoreEncodingErrors));
        // objects.add(fileObject);
        return bytes.toString(StandardCharsets.US_ASCII);
    }
    
    @Override
    public InputStream openInputStream() throws IOException {
        System.err.println(DebugPrinter.toString("InMemoryJavaClassFileObject::openInputStream"));
        // objects.add(fileObject);
        return bytes.toInputStream();
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        System.err.println(DebugPrinter.toString("InMemoryJavaClassFileObject::openOutputStream"));
        // objects.add(fileObject);
        return bytes;
    }
    
    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        System.err.println(DebugPrinter.toString("InMemoryJavaClassFileObject::isNameCompatible", simpleName, kind));
        return super.isNameCompatible(simpleName, kind);
    }
}
