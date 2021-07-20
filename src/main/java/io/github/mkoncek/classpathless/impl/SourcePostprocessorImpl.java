/*-
 * Copyright (c) 2021 Marián Konček
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
package io.github.mkoncek.classpathless.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import io.github.mkoncek.classpathless.api.CompilationError;
import io.github.mkoncek.classpathless.api.IdentifiedSource;
import io.github.mkoncek.classpathless.api.SourcePostprocessor;

public class SourcePostprocessorImpl implements SourcePostprocessor {
    static String makeAbstract(String source, long lineNum, long columnNum) {
        var result = new StringBuilder();

        for (var line : source.split("\\R")) {
            --lineNum;
            if (lineNum == 0) {
                line = line.substring(0, (int) columnNum - 1) + "abstract " + line.substring((int) columnNum - 1);
            }
            result.append(line);
            result.append(System.lineSeparator());
        }

        return result.toString();
    }

    @Override
    public Result postprocess(IdentifiedSource source,
            Collection<CompilationError> compilationErrors) {
        var copy = new ArrayList<>(compilationErrors);

        copy.sort((lhs, rhs) -> {
            int result = Long.compare(rhs.lineNum, lhs.lineNum);
            if (result == 0) {
                result = Long.compare(rhs.columnNum, lhs.columnNum);
            }
            return result;
        });

        var result = new Result(source, false);


        for (var ce : copy) {
            if (ce.errorCode.equals("compiler.err.does.not.override.abstract")) {
                var modifiedSource = makeAbstract(result.source.getSourceCode(), ce.lineNum, ce.columnNum);
                result = new Result(new IdentifiedSource(result.source.getClassIdentifier(),
                        modifiedSource.getBytes(StandardCharsets.UTF_8)), true);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        String lol = "dasdasd\nerjferf\r\nasdasdad\n\n\ndewdewd";

        for (var line : lol.split("\\R")) {
            System.out.print("\"");
            System.out.print(line);
            System.out.print("\"");
            System.out.println();
        }
    }
}
