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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.logging.Level;

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.ClassesProvider;
import io.github.mkoncek.classpathless.api.IdentifiedBytecode;

public class Utils {
    static Collection<IdentifiedBytecode> pullClasses(SortedSet<String> availableClasses, String importName, boolean recurse,
            ClassesProvider classprovider, LoggingSwitch loggingSwitch) throws IOException {
        var result = new ArrayList<IdentifiedBytecode>();

        for (var availableClassName : availableClasses.tailSet(importName)) {
            if (!availableClassName.startsWith(importName)) {
                break;
            }

            if (availableClassName.length() > importName.length()
                    && availableClassName.charAt(importName.length()) != '.') {
                break;
            }

            if (availableClassName.contains("$$Lambda$")) {
                loggingSwitch.logln(Level.FINE, "Ignoring lambda class \"{0}\"", availableClassName);
                continue;
            }

            loggingSwitch.logln(Level.FINE, "Pulling class from ClassProvider: \"{0}\"", availableClassName);
            result.addAll(classprovider.getClass(new ClassIdentifier(availableClassName)));
        }

        return result;
    }
}
