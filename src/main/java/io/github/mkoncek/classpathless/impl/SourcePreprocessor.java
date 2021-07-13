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

import java.util.Collection;

import io.github.mkoncek.classpathless.api.IdentifiedSource;

public interface SourcePreprocessor {
    Collection<IdentifiedSource> process(Collection<IdentifiedSource> sources);

    public static class NoSourcePreprocessor implements SourcePreprocessor {
        @Override
        public Collection<IdentifiedSource> process(Collection<IdentifiedSource> sources) {
            for (var source : sources) {
                System.out.print("from preprocessor ");
                System.out.println(source.getClassIdentifier().getFullName());
            }

            return sources;
        }
    }
}
