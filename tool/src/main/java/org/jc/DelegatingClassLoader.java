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
package org.jc;

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
	public ClassLoader delegate;
	
	public DelegatingClassLoader(ClassLoader delegate)
	{
		super("Delegating_" + delegate.getName(), delegate);
		System.err.println("\t\t###");
		System.err.println(delegate);
		this.delegate = delegate;
		System.err.println(this.delegate);
	}
	
	@Override
	public int hashCode()
	{
		return delegate.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return delegate.equals(obj);
	}
	
	@Override
	public String toString()
	{
		return delegate.toString();
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::loadClass", name));
		return delegate.loadClass(name);
	}
	
	@Override
	public URL getResource(String name)
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::getResource", name));
		return delegate.getResource(name);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::getResources", name));
		return delegate.getResources(name);
	}
	
	@Override
	public Stream<URL> resources(String name)
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::resources", name));
		return delegate.resources(name);
	}
	
	@Override
	public InputStream getResourceAsStream(String name)
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::getResourceAsStream", name));
		return delegate.getResourceAsStream(name);
	}
	
	@Override
	public void setDefaultAssertionStatus(boolean enabled)
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::setDefaultAssertionStatus", enabled));
		delegate.setDefaultAssertionStatus(enabled);
	}
	
	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled)
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::setPackageAssertionStatus", packageName, enabled));
		delegate.setPackageAssertionStatus(packageName, enabled);
	}
	
	@Override
	public void setClassAssertionStatus(String className, boolean enabled)
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::setClassAssertionStatus", className, enabled));
		delegate.setClassAssertionStatus(className, enabled);
	}
	
	@Override
	public void clearAssertionStatus()
	{
		System.err.println(DebugPrinter.to_string("DelegatingClassLoader::clearAssertionStatus"));
		delegate.clearAssertionStatus();
	}
}
