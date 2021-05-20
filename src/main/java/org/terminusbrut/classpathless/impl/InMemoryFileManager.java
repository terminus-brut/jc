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
package org.terminusbrut.classpathless.impl;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

/**
 * @author Marián Konček
 */
public class InMemoryFileManager implements JavaFileManager {
    static final String CP_SEPARATOR = System.getProperty("path.separator");

    private JavaFileManager delegate;
    private List<InMemoryJavaClassFileObject> classes = new ArrayList<>();
    public List<String> classpath = Collections.emptyList();

    public Collection<InMemoryJavaClassFileObject> classOutputs() {
        return classes;
    }

    public InMemoryFileManager(JavaFileManager delegate, String classpath) {
        super();
        this.delegate = delegate;

        if (classpath != null) {
            this.classpath = Arrays.asList(classpath.split(CP_SEPARATOR));
        }
    }

    @Override
    public Location getLocationForModule(Location location, String moduleName)
            throws IOException {
        System.err.println(DebugPrinter.fromStack(location, moduleName));
        return delegate.getLocationForModule(location, moduleName);
    }

    @Override
    public Location getLocationForModule(Location location, JavaFileObject fo)
            throws IOException {
        System.err.println(DebugPrinter.fromStack(location, fo));
        return delegate.getLocationForModule(location, fo);
    }

    @Override
    public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service)
            throws IOException {
        System.err.println(DebugPrinter.fromStack(location, service));
        return delegate.getServiceLoader(location, service);
    }

    @Override
    public String inferModuleName(Location location) throws IOException {
        System.err.println(DebugPrinter.fromStack(location));
        return delegate.inferModuleName(location);
    }

    @Override
    public Iterable<Set<Location>> listLocationsForModules(Location location)
            throws IOException {
        System.err.println(DebugPrinter.fromStack(location));
        return delegate.listLocationsForModules(location);
    }

    @Override
    public boolean contains(Location location, FileObject fo) throws IOException {
        System.err.println(DebugPrinter.fromStack(location, fo));
        return delegate.contains(location, fo);
    }

    @Override
    public void close() throws IOException {
        System.err.println(DebugPrinter.fromStack());
        delegate.close();
    }

    @Override
    public void flush() throws IOException {
        System.out.println(DebugPrinter.fromStack());
        delegate.flush();
    }

    @Override
    public int isSupportedOption(String option) {
        System.err.println(DebugPrinter.fromStack(option));
        return delegate.isSupportedOption(option);
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        System.err.println(DebugPrinter.fromStack(a, b));
        return delegate.isSameFile(a, b);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                System.err.println(DebugPrinter.fromStack(location));
                return new DelegatingClassLoader(delegate.getClassLoader(location));
            }
        });
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName,
            String relativeName) throws IOException {
        System.err.println(DebugPrinter.fromStack(location, packageName, relativeName));
        return delegate.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName,
            String relativeName, FileObject sibling) throws IOException {
        System.err.println(DebugPrinter.fromStack(packageName, relativeName, sibling));
        return delegate.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location,
            String className, Kind kind) throws IOException {
        System.err.println(DebugPrinter.fromStack(location, className, kind));
        return delegate.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
            String className, Kind kind, FileObject sibling) throws IOException {
        System.err.println(DebugPrinter.fromStack(location, className, kind, sibling));

        if (kind == Kind.CLASS && location == StandardLocation.CLASS_OUTPUT) {
            final var result = new InMemoryJavaClassFileObject(
                    sibling.getName().substring(1)
                    );
            classes.add(result);

            System.out.println("\t" + classes.size());

            return result;
        } else {
            return delegate.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    @Override
    public boolean hasLocation(Location location) {
        System.err.print(DebugPrinter.fromStack(location));
        var result = delegate.hasLocation(location);
        System.err.print(", returning: ");
        System.err.println(result);
        return result;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        System.err.print(DebugPrinter.fromStack(location, file));
        var result = delegate.inferBinaryName(location, file);
        System.err.print(", returning: ");
        System.err.println(result);
        return result;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName,
            Set<Kind> kinds, boolean recurse) throws IOException {
        System.err.print(DebugPrinter.fromStack(location, packageName, kinds, recurse));
        var result = delegate.list(location, packageName, kinds, recurse);
        System.err.print(", returning: ");
        System.err.println(result);

        /*
        if (location.equals(StandardLocation.CLASS_PATH) || location.equals(StandardLocation.SOURCE_PATH)) {

            var result = new ArrayList<JavaFileObject>();
            result.add(new InMemoryJavaClassFileObject("asd"));
            return result;
        }
         */

        return result;
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        System.err.println(DebugPrinter.fromStack(current, remaining));
        return delegate.handleOption(current, remaining);
    }
}
