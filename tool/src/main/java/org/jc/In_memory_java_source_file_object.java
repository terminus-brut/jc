package org.jc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import javax.tools.SimpleJavaFileObject;

import org.apache.commons.io.IOUtils;

public class In_memory_java_source_file_object extends SimpleJavaFileObject
{
	String source;
	
	In_memory_java_source_file_object(String name, String source)
	{
		super(URI.create("string:///" + name), Kind.SOURCE);
		this.source = source;
	}
	
	In_memory_java_source_file_object(String name, InputStream is) throws IOException
	{
		this(name, IOUtils.toString(is, (String) null));
	}
	
	In_memory_java_source_file_object(Path path) throws IOException
	{
		this(path.getFileName().toString(), new FileInputStream(path.toFile()));
	}
	
	@Override
	public CharSequence getCharContent(boolean arg0) throws IOException
	{
		return source;
	}
}
