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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import io.github.mkoncek.classpathless.api.ClassIdentifier;
import io.github.mkoncek.classpathless.api.ClassesProvider;
import io.github.mkoncek.classpathless.api.IdentifiedBytecode;

public class UtilsTest {
    static class SimpleClassesProvider implements ClassesProvider {
        private Map<String, IdentifiedBytecode> bytecodes = new TreeMap<>();

        public SimpleClassesProvider(Collection<IdentifiedBytecode> bytecodes) {
            for (var bytecode : bytecodes) {
                this.bytecodes.put(bytecode.getClassIdentifier().getFullName(), bytecode);
            }
        }

        @Override
        public Collection<IdentifiedBytecode> getClass(
                ClassIdentifier... names) {
            var result = new ArrayList<IdentifiedBytecode>();

            for (var name : names) {
                var bytecode = bytecodes.get(name.getFullName());
                if (bytecode != null) {
                    if (name.equals(bytecode.getClassIdentifier())) {
                        result.add(new IdentifiedBytecode(name, bytecode.getFile()));
                    }
                }
            }

            return result;
        }

        @Override
        public List<String> getClassPathListing() {
            return new ArrayList<>(bytecodes.keySet());
        }
    }

    @Test
    public void testPullSimple() {

    }
}
