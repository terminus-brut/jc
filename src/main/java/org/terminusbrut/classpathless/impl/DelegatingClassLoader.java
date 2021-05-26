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
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.stream.Stream;

/**
 * @author Marián Konček
 */
public class DelegatingClassLoader extends ClassLoader
{
    public DelegatingClassLoader(ClassLoader delegate) {
        super("Delegating_" + delegate.getName(), delegate);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.loadClass(name);
    }

    @Override
    public URL getResource(String name) {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.getResources(name);
    }

    @Override
    public Stream<URL> resources(String name) {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.resources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.getResourceAsStream(name);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        Compiler.verbose.println(DebugPrinter.fromStack(enabled));
        super.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        Compiler.verbose.println(DebugPrinter.fromStack(packageName, enabled));
        super.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        Compiler.verbose.println(DebugPrinter.fromStack(className, enabled));
        super.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        Compiler.verbose.println(DebugPrinter.fromStack());
        super.clearAssertionStatus();
    }

    @Override
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor,
            String implTitle, String implVersion, String implVendor, URL sealBase) {
        Compiler.verbose.println(DebugPrinter.fromStack(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase));
        return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    @Override
    protected Class<?> findClass(String moduleName, String name) {
        Compiler.verbose.println(DebugPrinter.fromStack(moduleName, name));
        return super.findClass(moduleName, name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.findClass(name);
    }

    @Override
    protected String findLibrary(String libname) {
        Compiler.verbose.println(DebugPrinter.fromStack(libname));
        return super.findLibrary(libname);
    }

    @Override
    protected URL findResource(String moduleName, String name) throws IOException {
        Compiler.verbose.println(DebugPrinter.fromStack(moduleName, name));
        return super.findResource(moduleName, name);
    }

    @Override
    protected URL findResource(String name) {
        Compiler.verbose.println(DebugPrinter.fromStack());
        return super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        Compiler.verbose.println(DebugPrinter.fromStack());
        return super.findResources(name);
    }

    @Override
    protected Object getClassLoadingLock(String className) {
        Compiler.verbose.println(DebugPrinter.fromStack());
        return super.getClassLoadingLock(className);
    }

    @Override
    protected Package getPackage(String name) {
        Compiler.verbose.println(DebugPrinter.fromStack(name));
        return super.getPackage(name);
    }

    @Override
    protected Package[] getPackages() {
        Compiler.verbose.println(DebugPrinter.fromStack());
        return super.getPackages();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Compiler.verbose.println(DebugPrinter.fromStack(name, resolve));
        return super.loadClass(name, resolve);
    }
}
