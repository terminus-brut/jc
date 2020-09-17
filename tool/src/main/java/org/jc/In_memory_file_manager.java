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
	public Location getLocationForModule(Location location, String moduleName) throws IOException
	{
		System.err.println("getLocationForModule: ");
		return delegate.getLocationForModule(location, moduleName);
	}
	
	@Override
	public Location getLocationForModule(Location location, JavaFileObject fo) throws IOException
	{
		System.err.println("getLocationForModule: ");
		return delegate.getLocationForModule(location, fo);
	}
	
	@Override
	public <S> ServiceLoader<S> getServiceLoader(Location location, Class<S> service) throws IOException
	{
		System.err.println("getServiceLoader: ");
		return delegate.getServiceLoader(location, service);
	}
	
	@Override
	public String inferModuleName(Location location) throws IOException
	{
		System.err.println("inferModuleName: ");
		return delegate.inferModuleName(location);
	}
	
	@Override
	public Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException
	{
		System.err.println("listLocationsForModules: " + location.toString());
		return delegate.listLocationsForModules(location);
	}
	
	@Override
	public boolean contains(Location location, FileObject fo) throws IOException
	{
		System.err.println("contains: ");
		return delegate.contains(location, fo);
	}
	
	@Override
	public void close() throws IOException
	{
		System.err.println("close: ");
		delegate.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		System.err.println("flush: ");
		
		delegate.flush();
	}
	
	@Override
	public int isSupportedOption(String option)
	{
		System.err.println("isSupportedOption: " + option);
		return delegate.isSupportedOption(option);
	}
	
	@Override
	public boolean isSameFile(FileObject arg0, FileObject arg1)
	{
		System.err.println("isSameFile: ");
		return delegate.isSameFile(arg0, arg1);
	}
	
	@Override
	public ClassLoader getClassLoader(Location location)
	{
		System.err.println("getClassLoader: " + location.toString());
		return delegate.getClassLoader(location);
	}
	
	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName)
			throws IOException
	{
		System.err.println("getFileForInput: ");
		return delegate.getFileForInput(location, packageName, relativeName);
	}
	
	@Override
	public FileObject getFileForOutput(Location location, String packageName, String relativeName,
			FileObject sibling) throws IOException
	{
		System.err.println("getFileForOutput: " + location.toString() + ", " + packageName + ", " + relativeName.toString() + ", " + sibling.toString());
		return delegate.getFileForOutput(location, packageName, relativeName, sibling);
	}
	
	@Override
	public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind)
			throws IOException
	{
		System.err.println("getJavaFileForInput: " + className);
		return delegate.getJavaFileForInput(location, className, kind);
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
			FileObject sibling) throws IOException
	{
		System.err.println("getJavaFileForOutput: " + location.toString() + ", " + className + ", " + kind.toString() + ", " + sibling.toString());
		
		if (kind == Kind.CLASS && location == StandardLocation.CLASS_OUTPUT)
		{
			final var result = new In_memory_java_class_file_object(sibling.getName().substring(1));
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
		System.err.println("hasLocation: " + location.toString());
		return delegate.hasLocation(location);
	}
	
	@Override
	public String inferBinaryName(Location location, JavaFileObject file)
	{
		System.err.println("inferBinaryName: " + location.toString());
		return delegate.inferBinaryName(location, file);
	}
	
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds,
			boolean recurse) throws IOException
	{
		System.err.println("list: ");
		return delegate.list(location, packageName, kinds, recurse);
	}
	
	@Override
	public boolean handleOption(String current, Iterator<String> remaining)
	{
		System.err.println("handleOption: ");
		return delegate.handleOption(current, remaining);
	}
}
