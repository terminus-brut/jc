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

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.ClassesProvider;

/**
 * @author Marián Konček
 */
public class InMemoryFileManager implements JavaFileManager {
    private JavaFileManager delegate;
    private ClassesProvider classprovider = null;
    private SortedSet<String> availableClasses = null;
    private LoggingSwitch loggingSwitch = null;

    private ArrayList<InMemoryJavaClassFileObject> classes = new ArrayList<>();
    private TreeMap<String, InMemoryJavaClassFileObject> nameToBytecode = new TreeMap<>();

    void setLoggingSwitch(LoggingSwitch loggingSwitch) {
        this.loggingSwitch = loggingSwitch;
    }

    void setClassProvider(ClassesProvider classprovider) {
        this.classprovider = classprovider;
    }

    void setAvailableClasses(SortedSet<String> availableClasses) {
        this.availableClasses = availableClasses;
    }

    void clearAndGetOutput(Collection<JavaFileObject> classOutput) {
        System.out.println("#### " + classes.size());
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
        loggingSwitch.trace(this, "getClassLoadingLock", location, moduleName);
        var result = delegate.getLocationForModule(location, moduleName);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public Location getLocationForModule(Location location, JavaFileObject fo)
            throws IOException {
        loggingSwitch.trace(this, "getLocationForModule", location, fo);
        var result = delegate.getLocationForModule(location, fo);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service)
            throws IOException {
        loggingSwitch.trace(this, "getServiceLoader", location, service);
        var result = delegate.getServiceLoader(location, service);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public String inferModuleName(Location location) throws IOException {
        loggingSwitch.trace(this, "inferModuleName", location);
        var result = delegate.inferModuleName(location);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public Iterable<Set<Location>> listLocationsForModules(Location location)
            throws IOException {
        loggingSwitch.trace(this, "listLocationsForModules", location);
        var result = delegate.listLocationsForModules(location);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public boolean contains(Location location, FileObject fo) throws IOException {
        loggingSwitch.trace(this, "contains", location, fo);
        var result = delegate.contains(location, fo);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public void close() throws IOException {
        loggingSwitch.trace(this, "close");
        delegate.close();
    }

    @Override
    public void flush() throws IOException {
        loggingSwitch.trace(this, "flush");
        delegate.flush();
    }

    @Override
    public int isSupportedOption(String option) {
        loggingSwitch.trace(this, "isSupportedOption", option);
        var result = delegate.isSupportedOption(option);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        loggingSwitch.trace(this, "isSameFile", a, b);
        var result = delegate.isSameFile(a, b);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        loggingSwitch.trace(this, "getClassLoader", location);
        var result = delegate.getClassLoader(location);
        loggingSwitch.trace(result);
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return new DelegatingClassLoader(result, loggingSwitch);
            }
        });
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName,
            String relativeName) throws IOException {
        loggingSwitch.trace(this, "getFileForInput", location, packageName, relativeName);
        var result = delegate.getFileForInput(location, packageName, relativeName);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName,
            String relativeName, FileObject sibling) throws IOException {
        loggingSwitch.trace(this, "getFileForOutput", packageName, relativeName, sibling);
        var result = delegate.getFileForOutput(location, packageName, relativeName, sibling);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location,
            String className, Kind kind) throws IOException {
        loggingSwitch.trace(this, "getJavaFileForInput", location, className, kind);
        var result = delegate.getJavaFileForInput(location, className, kind);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
            String className, Kind kind, FileObject sibling) throws IOException {
        loggingSwitch.trace(this, "getJavaFileForOutput", location, className, kind, sibling);
        if (kind == Kind.CLASS && location == StandardLocation.CLASS_OUTPUT) {
            var result = new InMemoryJavaClassFileObject(className);
            loggingSwitch.trace(result);
            classes.add(result);
            return result;
        } else {
            var result = delegate.getJavaFileForOutput(location, className, kind, sibling);
            loggingSwitch.trace(result);
            return result;
        }
    }

    @Override
    public boolean hasLocation(Location location) {
        loggingSwitch.trace(this, "hasLocation", location);
        var result = delegate.hasLocation(location);
        loggingSwitch.trace(result);
        return result;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        loggingSwitch.trace(this, "inferBinaryName", location, file);
        if (file instanceof InMemoryJavaClassFileObject) {
            var realFile = (InMemoryJavaClassFileObject) file;
            var result = realFile.toUri().toString();
            loggingSwitch.trace(result);
            /// Remove "class:///"
            result = result.substring(9);
            return result;
        } else {
            var result = delegate.inferBinaryName(location, file);
            loggingSwitch.trace(result);
            return result;
        }
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName,
            Set<Kind> kinds, boolean recurse) throws IOException {
        loggingSwitch.trace(this, "list", location, packageName, kinds, recurse);
        if (location.equals(StandardLocation.CLASS_PATH) || location.equals(StandardLocation.SOURCE_PATH)) {
            var result = new ArrayList<JavaFileObject>();

            for (var availableClassName : availableClasses.tailSet(packageName)) {
                if (!availableClassName.startsWith(packageName)) {
                    break;
                }

                if (availableClassName.contains("$$Lambda$")) {
                    loggingSwitch.logln(Level.FINE, "Ignoring lambda class \"{0}\"", availableClassName);
                    break;
                }

                /// Remove package name + "."
                var shortName = availableClassName.substring(packageName.length() + 1);
                if (!recurse && shortName.contains(".")) {
                    continue;
                }

                loggingSwitch.logln(Level.FINE, "Pulling class from ClassProvider: \"{0}\"", availableClassName);
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
            loggingSwitch.trace(result);
            return result;
        } else {
            var result = delegate.list(location, packageName, kinds, recurse);
            loggingSwitch.trace(result);
            return result;
        }
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        loggingSwitch.trace(this, "handleOption", current, remaining);
        var result = delegate.handleOption(current, remaining);
        loggingSwitch.trace(result);
        return result;
    }
}
