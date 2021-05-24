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
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import org.terminusbrut.classpathless.api.ClassIdentifier;
import org.terminusbrut.classpathless.api.ClassesProvider;

/**
 * @author Marián Konček
 */
public class InMemoryFileManager implements JavaFileManager {
    private JavaFileManager delegate;
    private ClassesProvider classprovider = null;
    private SortedSet<String> availableClasses = null;

    private ArrayList<InMemoryJavaClassFileObject> classes = new ArrayList<>();
    private TreeMap<String, InMemoryJavaClassFileObject> nameToBytecode = new TreeMap<>();

    void setClassProvider(ClassesProvider classprovider) {
        this.classprovider = classprovider;
    }

    void setAvailableClasses(SortedSet<String> availableClasses) {
        this.availableClasses = availableClasses;
    }

    void clearAndGetOutput(Collection<JavaFileObject> classOutput) {
        classOutput.addAll(classes);
        classes.clear();
        nameToBytecode.clear();
        availableClasses.clear();
    }

    public InMemoryFileManager(JavaFileManager delegate) {
        super();
        this.delegate = delegate;
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
        System.err.println(DebugPrinter.fromStack(location));
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
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
            System.out.println(sibling.getName());
            System.out.println(className);
            final var result = new InMemoryJavaClassFileObject(className);
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
        if (file instanceof InMemoryJavaClassFileObject) {
            System.err.print(DebugPrinter.fromStack(location, file));
            var realFile = (InMemoryJavaClassFileObject) file;
            var result = realFile.toUri().toString();
            /// Remove "class:///"
            result = result.substring(9, result.length());
            System.err.print(", returning: ");
            System.err.println(result);
            return result;
        }
        System.err.print(DebugPrinter.fromStack(location, file));
        var result = delegate.inferBinaryName(location, file);
        System.err.print(", returning: ");
        System.err.println(result);
        return result;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName,
            Set<Kind> kinds, boolean recurse) throws IOException {
        if (location.equals(StandardLocation.CLASS_PATH) || location.equals(StandardLocation.SOURCE_PATH)) {
            var result = new ArrayList<JavaFileObject>();

            for (var availableClassName : availableClasses.tailSet(packageName)) {
                if (!availableClassName.startsWith(packageName)) {
                    break;
                }

                /// Remove package name + "."
                var shortName = availableClassName.substring(packageName.length() + 1);
                if (!recurse && shortName.contains(".")) {
                    continue;
                }

                var found = classprovider.getClass(new ClassIdentifier(availableClassName));
                if (!found.isEmpty()) {
                    for (var identified : found) {
                        var classObject = new InMemoryJavaClassFileObject(availableClassName);
                        classObject.openOutputStream().write(identified.getFile());
                        result.add(classObject);
                        nameToBytecode.put(availableClassName, classObject);
                    }
                }
            }

            System.out.print(DebugPrinter.fromStack(location, packageName, kinds, recurse));
            System.out.print(", returning: ");
            System.out.println(result);
            return result;
        } else {
            System.err.print(DebugPrinter.fromStack(location, packageName, kinds, recurse));
            var result = delegate.list(location, packageName, kinds, recurse);
            System.err.print(", returning: ");
            System.err.println(result);
            return result;
        }
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        System.err.println(DebugPrinter.fromStack(current, remaining));
        return delegate.handleOption(current, remaining);
    }
}
