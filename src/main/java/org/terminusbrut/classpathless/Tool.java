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
package org.terminusbrut.classpathless;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.terminusbrut.classpathless.impl.Compiler;
import org.terminusbrut.classpathless.impl.InMemoryJavaSourceFileObject;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Marián Konček
 */
public class Tool {
    final static String CP_SEPARATOR = System.getProperty("path.separator");

    static class Arguments {
        @Parameter(names = {"-h", "--help"}, help = true, description =
                "Display help.")
        boolean help = false;

        @Parameter(names = {"-cp", "--classpath"})
        String classpath = null;

        @Parameter(names = {"-i", "--inputs"}, required = true)
        List<String> inputs = new ArrayList<>();

        @Parameter(names = {"-o", "--outdir"})
        String output = ".";
    }

    public static void main(String[] args) throws IOException {
        var arguments = new Arguments();
        var jcommander = JCommander.newBuilder().addObject(arguments).build();
        jcommander.parse(args);

        if (arguments.help) {
            jcommander.usage();
            return;
        }

        var compiler = new Compiler();

        for (final var arg : arguments.inputs) {
            compiler.add(new InMemoryJavaSourceFileObject(Paths.get(arg)));
        }

        compiler.compile(Paths.get(arguments.output));
    }
}
