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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class Example {
    static class FM implements JavaFileManager {
        JavaFileManager delegate;

        FM(JavaFileManager delegate) {
            this.delegate = delegate;
        }

        @Override
        public int isSupportedOption(String option) {
            return delegate.isSupportedOption(option);
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return delegate.getClassLoader(location);
        }

        @Override
        public Iterable<JavaFileObject> list(Location location,
                String packageName, Set<Kind> kinds, boolean recurse)
                        throws IOException {
            System.out.print(location);
            System.out.print(", ");
            System.out.print(packageName);
            System.out.print(", ");
            System.out.print(kinds);
            System.out.println();
            return delegate.list(location, packageName, kinds, recurse);
        }

        @Override
        public String inferBinaryName(Location location, JavaFileObject file) {
            return delegate.inferBinaryName(location, file);
        }

        @Override
        public boolean isSameFile(FileObject a, FileObject b) {
            return delegate.isSameFile(a, b);
        }

        @Override
        public boolean handleOption(String current,
                Iterator<String> remaining) {
            return delegate.handleOption(current,remaining);
        }

        @Override
        public boolean hasLocation(Location location) {
            return delegate.hasLocation(location);
        }

        @Override
        public JavaFileObject getJavaFileForInput(Location location,
                String className, Kind kind) throws IOException {
            return delegate.getJavaFileForInput(location, className, kind);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
                String className, Kind kind, FileObject sibling)
                        throws IOException {
            return delegate.getJavaFileForOutput(location, className, kind, sibling);
        }

        @Override
        public FileObject getFileForInput(Location location, String packageName,
                String relativeName) throws IOException {
            return delegate.getFileForInput(location, packageName, relativeName);
        }

        @Override
        public FileObject getFileForOutput(Location location,
                String packageName, String relativeName, FileObject sibling)
                        throws IOException {
            return delegate.getFileForOutput(location, packageName, relativeName, sibling);
        }

        @Override
        public Iterable<Set<Location>> listLocationsForModules(
                Location location) throws IOException {
            return delegate.listLocationsForModules(location);
        }

        @Override
        public String inferModuleName(Location location) throws IOException {
            return delegate.inferModuleName(location);
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }

    public static void main(String[] args) throws IOException {
        var javac = ToolProvider.getSystemJavaCompiler();
        var fm = javac.getStandardFileManager(null, null, null);
        for (var f : fm.list(StandardLocation.CLASS_PATH,
                "org.apache.commons.io", Set.of(Kind.CLASS, Kind.SOURCE, Kind.OTHER, Kind.HTML), false)) {
            System.out.println(f);
        }

        var fileObjects = fm.getJavaFileObjects(Paths.get("src/test/resources/package-root/a/b/c/A.java"));
        System.out.println(javac.getTask(null, new FM(fm), null, null, null, fileObjects).call());
    }
}
