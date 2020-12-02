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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import javax.tools.SimpleJavaFileObject;

import org.apache.commons.io.IOUtils;

/**
 * @author Marián Konček
 */
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
		System.err.println(Debug_printer.to_string("getCharContent", arg0));
		return source;
	}
}
