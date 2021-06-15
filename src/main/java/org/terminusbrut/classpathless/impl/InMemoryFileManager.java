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
import java.util.logging.Level;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import org.terminusbrut.classpathless.api.ClassIdentifier;
import org.terminusbrut.classpathless.api.ClassesProvider;
import org.terminusbrut.classpathless.api.MessagesListener;

/**
 * @author Marián Konček
 */
public class InMemoryFileManager implements JavaFileManager {
    private JavaFileManager delegate;
    private ClassesProvider classprovider = null;
    private SortedSet<String> availableClasses = null;
    private MessagesListener messagesListener;

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

    public InMemoryFileManager(JavaFileManager delegate, MessagesListener messagesListener) {
        super();
        this.delegate = delegate;
        this.messagesListener = messagesListener;
    }

    @Override
    public Location getLocationForModule(Location location, String moduleName)
            throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getLocationForModule", location, moduleName));
        return delegate.getLocationForModule(location, moduleName);
    }

    @Override
    public Location getLocationForModule(Location location, JavaFileObject fo)
            throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getLocationForModule", location, fo));
        return delegate.getLocationForModule(location, fo);
    }

    @Override
    public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service)
            throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getServiceLoader", location, service));
        return delegate.getServiceLoader(location, service);
    }

    @Override
    public String inferModuleName(Location location) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("inferModuleName", location));
        return delegate.inferModuleName(location);
    }

    @Override
    public Iterable<Set<Location>> listLocationsForModules(Location location)
            throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("listLocationsForModules", location));
        return delegate.listLocationsForModules(location);
    }

    @Override
    public boolean contains(Location location, FileObject fo) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("contains", location, fo));
        return delegate.contains(location, fo);
    }

    @Override
    public void close() throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("close"));
        delegate.close();
    }

    @Override
    public void flush() throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("flush"));
        delegate.flush();
    }

    @Override
    public int isSupportedOption(String option) {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("isSupportedOption"));
        return delegate.isSupportedOption(option);
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("isSameFile", a, b));
        return delegate.isSameFile(a, b);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getClassLoader", location));
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return new DelegatingClassLoader(delegate.getClassLoader(location), messagesListener);
            }
        });
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName,
            String relativeName) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getFileForInput", location, packageName, relativeName));
        return delegate.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName,
            String relativeName, FileObject sibling) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getFileForOutput", packageName, relativeName, sibling));
        return delegate.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location,
            String className, Kind kind) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getJavaFileForInput", location, className, kind));
        return delegate.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
            String className, Kind kind, FileObject sibling) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("getJavaFileForOutput", location, className, kind, sibling));

        if (kind == Kind.CLASS && location == StandardLocation.CLASS_OUTPUT) {
            final var result = new InMemoryJavaClassFileObject(className);
            classes.add(result);
            return result;
        } else {
            return delegate.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    @Override
    public boolean hasLocation(Location location) {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("hasLocation", location));
        var result = delegate.hasLocation(location);
        return result;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("inferBinaryName", location, file));
        if (file instanceof InMemoryJavaClassFileObject) {
            var realFile = (InMemoryJavaClassFileObject) file;
            var result = realFile.toUri().toString();
            /// Remove "class:///"
            result = result.substring(9, result.length());
            return result;
        }
        var result = delegate.inferBinaryName(location, file);
        return result;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName,
            Set<Kind> kinds, boolean recurse) throws IOException {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("list", location, packageName, kinds, recurse));
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
            return result;
        } else {
            var result = delegate.list(location, packageName, kinds, recurse);
            return result;
        }
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        messagesListener.addMessage(Level.FINEST, DebugPrinter.print("handleOption", current, remaining));
        return delegate.handleOption(current, remaining);
    }
}
