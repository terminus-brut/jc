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
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::loadClass", name));
		return super.loadClass(name);
	}
	
	@Override
	public URL getResource(String name) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::getResource", name));
		return super.getResource(name);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::getResources", name));
		return super.getResources(name);
	}
	
	@Override
	public Stream<URL> resources(String name) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::resources", name));
		return super.resources(name);
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::getResourceAsStream", name));
		return super.getResourceAsStream(name);
	}
	
	@Override
	public void setDefaultAssertionStatus(boolean enabled) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::setDefaultAssertionStatus", enabled));
		super.setDefaultAssertionStatus(enabled);
	}
	
	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::setPackageAssertionStatus", packageName, enabled));
		super.setPackageAssertionStatus(packageName, enabled);
	}
	
	@Override
	public void setClassAssertionStatus(String className, boolean enabled) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::setClassAssertionStatus", className, enabled));
		super.setClassAssertionStatus(className, enabled);
	}
	
	@Override
	public void clearAssertionStatus() {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::clearAssertionStatus"));
		super.clearAssertionStatus();
	}
	
	@Override
	protected Package definePackage(String name, String specTitle, String specVersion, String specVendor,
			String implTitle, String implVersion, String implVendor, URL sealBase) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::definePackage", name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase));
		return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
	}
	
	@Override
	protected Class<?> findClass(String moduleName, String name) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::findClass", moduleName, name));
		return super.findClass(moduleName, name);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::findClass", name));
		return super.findClass(name);
	}
	
	@Override
	protected String findLibrary(String libname) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::findLibrary", libname));
		return super.findLibrary(libname);
	}
	
	@Override
	protected URL findResource(String moduleName, String name) throws IOException {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::findResource", moduleName, name));
		return super.findResource(moduleName, name);
	}
	
	@Override
	protected URL findResource(String name) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::findResource"));
		return super.findResource(name);
	}
	
	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::findResources"));
		return super.findResources(name);
	}
	
	@Override
	protected Object getClassLoadingLock(String className) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::getClassLoadingLock"));
		return super.getClassLoadingLock(className);
	}
	
	@Override
	protected Package getPackage(String name) {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::getPackage", name));
		return super.getPackage(name);
	}
	
	@Override
	protected Package[] getPackages() {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::getPackages"));
		return super.getPackages();
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		System.err.println(DebugPrinter.toString("DelegatingClassLoader::loadClass", name, resolve));
		return super.loadClass(name, resolve);
	}
}
