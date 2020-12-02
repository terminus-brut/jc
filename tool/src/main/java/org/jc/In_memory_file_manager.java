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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
public class In_memory_file_manager implements JavaFileManager
{
	JavaFileManager delegate;
	ArrayList<In_memory_java_class_file_object> classes = new ArrayList<>();
	
	public Collection<In_memory_java_class_file_object> class_outputs()
	{
		return classes;
	}
	
	public In_memory_file_manager(JavaFileManager delegate)
	{
		super();
		this.delegate = delegate;
	}
	
	@Override
	public Location getLocationForModule(Location location, String moduleName)
			throws IOException
	{
		System.err.println(Debug_printer.to_string("getLocationForModule", location, moduleName));
		return delegate.getLocationForModule(location, moduleName);
	}
	
	@Override
	public Location getLocationForModule(Location location, JavaFileObject fo)
			throws IOException
	{
		System.err.println(Debug_printer.to_string("getLocationForModule", location, fo));
		return delegate.getLocationForModule(location, fo);
	}
	
	@Override
	public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service)
			throws IOException
	{
		System.err.println(Debug_printer.to_string("getServiceLoader", location, service));
		return delegate.getServiceLoader(location, service);
	}
	
	@Override
	public String inferModuleName(Location location) throws IOException
	{
		System.err.println(Debug_printer.to_string("inferModuleName", location));
		return delegate.inferModuleName(location);
	}
	
	@Override
	public Iterable<Set<Location>> listLocationsForModules(Location location)
			throws IOException
	{
		System.err.println(Debug_printer.to_string("listLocationsForModules", location));
		return delegate.listLocationsForModules(location);
	}
	
	@Override
	public boolean contains(Location location, FileObject fo) throws IOException
	{
		System.err.println(Debug_printer.to_string("contains", location, fo));
		return delegate.contains(location, fo);
	}
	
	@Override
	public void close() throws IOException
	{
		System.err.println(Debug_printer.to_string("close"));
		delegate.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		System.err.println(Debug_printer.to_string("flush"));
		delegate.flush();
	}
	
	@Override
	public int isSupportedOption(String option)
	{
		System.err.println(Debug_printer.to_string("isSupportedOption", option));
		return delegate.isSupportedOption(option);
	}
	
	@Override
	public boolean isSameFile(FileObject a, FileObject b)
	{
		System.err.println(Debug_printer.to_string("isSameFile", a, b));
		return delegate.isSameFile(a, b);
	}
	
	@Override
	public ClassLoader getClassLoader(Location location)
	{
		System.err.println(Debug_printer.to_string("getClassLoader", location));
		// return delegate.getClassLoader(location);
		var delegatel = delegate.getClassLoader(location);
		
		System.err.println(delegatel);
		var n = new Delegating_class_loader(delegatel, "asdasd");
		System.err.println(n.delegate);
		return n;
	}
	
	@Override
	public FileObject getFileForInput(Location location, String packageName,
			String relativeName) throws IOException
	{
		System.err.println(Debug_printer.to_string(
				"getFileForInput", location, packageName, relativeName));
		return delegate.getFileForInput(location, packageName, relativeName);
	}
	
	@Override
	public FileObject getFileForOutput(Location location, String packageName,
			String relativeName, FileObject sibling) throws IOException
	{
		System.err.println(Debug_printer.to_string(
				"getFileForOutput", packageName, relativeName, sibling));
		return delegate.getFileForOutput(location, packageName, relativeName, sibling);
	}
	
	@Override
	public JavaFileObject getJavaFileForInput(Location location,
			String className, Kind kind) throws IOException
	{
		System.err.println(Debug_printer.to_string(
				"getJavaFileForInput", location, className, kind));
		return delegate.getJavaFileForInput(location, className, kind);
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException
	{
		System.err.println(Debug_printer.to_string(
				"getJavaFileForOutput", location, className, kind, sibling));
		
		if (kind == Kind.CLASS && location == StandardLocation.CLASS_OUTPUT)
		{
			final var result = new In_memory_java_class_file_object(
					sibling.getName().substring(1)
			);
			classes.add(result);
			
			System.out.println("\t" + classes.size());
			
			return result;
		}
		else
		{
			return delegate.getJavaFileForOutput(location, className, kind, sibling);
		}
	}
	
	@Override
	public boolean hasLocation(Location location)
	{
		System.err.println(Debug_printer.to_string("hasLocation", location));
		return delegate.hasLocation(location);
	}
	
	@Override
	public String inferBinaryName(Location location, JavaFileObject file)
	{
		System.err.println(Debug_printer.to_string("inferBinaryName", location, file));
		return delegate.inferBinaryName(location, file);
	}
	
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<Kind> kinds, boolean recurse) throws IOException
	{
		System.err.println(Debug_printer.to_string("list", location, packageName, kinds, recurse));
		return delegate.list(location, packageName, kinds, recurse);
	}
	
	@Override
	public boolean handleOption(String current, Iterator<String> remaining)
	{
		System.err.println(Debug_printer.to_string("handleOption", current, remaining));
		return delegate.handleOption(current, remaining);
	}
}
