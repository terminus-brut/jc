/*-
 * Copyright (c) 2020 Red Hat, Inc.
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class Compiler
{
	final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	final In_memory_file_manager file_manager = new In_memory_file_manager(compiler.getStandardFileManager(null, null, null));
	final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	final List<JavaFileObject> compilation_units = new ArrayList<>();
	Path tempdir = Files.createTempDirectory("");
	
	Compiler() throws IOException
	{
		/*
		System.out.println(tempdir.toString());
		file_manager.setLocation(StandardLocation.CLASS_OUTPUT,
				Arrays.asList(tempdir.toFile()));
				*/
	}
	
	void add(JavaFileObject object)
	{
		compilation_units.add(object);
	}
	
	public void compile() throws IOException
	{
		if (! compiler.getTask(null,
				file_manager,
				null, null, null, compilation_units).call())
		{
			diagnostics.getDiagnostics().forEach(System.out::println);
			throw new RuntimeException("Could not compile file");
		}
		
		int i = 1;
		for (final var class_ : file_manager.class_outputs())
		{
			final var path = Paths.get("/home/mkoncek/Desktop/" + Integer.toString(i) + ".class");
			
			try (final var os = new FileOutputStream(path.toFile()))
			{
				os.write(class_.bytes.toByteArray());
			}
			
			++i;
		}
	}
}
