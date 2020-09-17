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
package tool;

import java.util.ArrayList;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Marián Konček
 */
public class Tool
{
	static class Arguments
	{
		@Parameter(names = {"-h", "--help"}, help = true, description =
				"Display help.")
		boolean help = false;
		
		@Parameter(names = {"-o", "--output"}, description =
				"The file to write the output to. " +
				"If not provided then outputs to the standard output.")
		String output_file = null;
		
		@Parameter(names = {"-c", "--config"}, description =
				"The file to read the configuration from.")
		String config_file = null;
		
		@Parameter(names = {"-f", "--files"}, variableArity = true, description =
				"The list of .rpm files to validate.")
		ArrayList<String> test_files = new ArrayList<>();
		
		@Parameter(names = {"-i", "--input"}, description =
				"The file to read the list of input files from.")
		String input_file = null;
		
		@Parameter(names = {"-r", "--color"}, description =
				"Print colored output.")
		boolean color = false;
		
		@Parameter(names = {"-v", "--verbose"}, description =
				"Print more detailed output (affected by --color as well).")
		boolean verbose = false;
		
		@Parameter(names = {"-n", "--only-failed"}, description =
				"Print only failed test cases.")
		boolean only_failed = false;
		
		@Parameter(names = {"-d", "--dump-config"}, description =
				"Print the XML configuration.")
		boolean dump_config = false;
	}
	
	public static void main(String[] args)
	{
		System.out.print
	}
}
