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
package org.terminusbrut.classpathless.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Marián Konček
 */
public class DebugPrinter {
    public static String Args(Object... args) {
        return "(" + Arrays.<Object>asList(args).stream()
                .map(o -> o == null ? "<null>" : o.toString())
                .collect(Collectors.joining(", ")) + ")";
    }

    public static String fromStack(Object... args) {
        var name = Thread.currentThread().getStackTrace()[2].toString();
        name = name.substring(0, name.indexOf('('));
        return name + Args(args);
    }
}