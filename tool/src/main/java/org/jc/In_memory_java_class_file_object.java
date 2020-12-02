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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * @author Marián Konček
 */
public class In_memory_java_class_file_object extends SimpleJavaFileObject
{
	class Bytes extends ByteArrayOutputStream
	{
		public byte[] buf()
		{
			return this.buf;
		}
	}
	
	Bytes bytes = new Bytes();
	
	public In_memory_java_class_file_object(String name)
	{
		super(URI.create("class:///" + name), Kind.CLASS);
	}
	
	public byte[] byte_array()
	{
		return bytes.buf();
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
	{
		System.err.println(Debug_printer.to_string("getCharContent", ignoreEncodingErrors));
		
		return bytes.toString();
	}
	
	@Override
	public OutputStream openOutputStream() throws IOException
	{
		System.err.println(Debug_printer.to_string("openOutputStream"));
		
		return bytes;
	}
	
	@Override
	public Writer openWriter() throws IOException
	{
		System.err.println(Debug_printer.to_string("openWriter"));
		
		return new OutputStreamWriter(openOutputStream());
	}
}
