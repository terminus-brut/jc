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
public class Delegating_class_loader extends ClassLoader
{
	public ClassLoader delegate;
	
	public Delegating_class_loader(ClassLoader delegate, String name)
	{
		super();
		System.err.println("\t\t###" + name);
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
	public String getName()
	{
		System.err.println(Debug_printer.to_string("getName"));
		System.err.println(delegate);
		return delegate.getName();
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		System.err.println(Debug_printer.to_string("loadClass", name));
		return delegate.loadClass(name);
	}
	
	@Override
	public URL getResource(String name)
	{
		System.err.println(Debug_printer.to_string("getResource", name));
		return delegate.getResource(name);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException
	{
		System.err.println(Debug_printer.to_string("getResources", name));
		return delegate.getResources(name);
	}
	
	@Override
	public Stream<URL> resources(String name)
	{
		System.err.println(Debug_printer.to_string("resources", name));
		return delegate.resources(name);
	}
	
	@Override
	public InputStream getResourceAsStream(String name)
	{
		System.err.println(Debug_printer.to_string("getResourceAsStream", name));
		return delegate.getResourceAsStream(name);
	}
	
	@Override
	public void setDefaultAssertionStatus(boolean enabled)
	{
		System.err.println(Debug_printer.to_string("setDefaultAssertionStatus", enabled));
		delegate.setDefaultAssertionStatus(enabled);
	}
	
	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled)
	{
		System.err.println(Debug_printer.to_string("setPackageAssertionStatus", packageName, enabled));
		delegate.setPackageAssertionStatus(packageName, enabled);
	}
	
	@Override
	public void setClassAssertionStatus(String className, boolean enabled)
	{
		System.err.println(Debug_printer.to_string("setClassAssertionStatus", className, enabled));
		delegate.setClassAssertionStatus(className, enabled);
	}
	
	@Override
	public void clearAssertionStatus()
	{
		System.err.println(Debug_printer.to_string("clearAssertionStatus"));
		delegate.clearAssertionStatus();
	}
}
