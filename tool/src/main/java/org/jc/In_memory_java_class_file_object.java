package org.jc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class In_memory_java_class_file_object extends SimpleJavaFileObject
{
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	
	public In_memory_java_class_file_object(String name)
	{
		super(URI.create("class:///" + name), Kind.CLASS);
	}
	
	public byte[] byte_array()
	{
		return bytes.toByteArray();
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
	{
		return bytes.toString();
	}
	
	@Override
	public OutputStream openOutputStream() throws IOException
	{
		System.err.println("openOutputStream()");
		
		return bytes;
	}
	
	@Override
	public Writer openWriter() throws IOException
	{
		System.err.println("openWriter()");
		
		return new OutputStreamWriter(openOutputStream());
	}
}
