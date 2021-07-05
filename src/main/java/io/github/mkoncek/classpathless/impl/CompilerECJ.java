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
import java.util.Optional;

import io.github.mkoncek.classpathless.api.ClassesProvider;
import io.github.mkoncek.classpathless.api.IdentifiedBytecode;
import io.github.mkoncek.classpathless.api.IdentifiedSource;
import io.github.mkoncek.classpathless.api.InMemoryCompiler;
import io.github.mkoncek.classpathless.api.MessagesListener;

/**
 * An implementation using Eclipse JDT compiler API
 */
public class CompilerECJ implements InMemoryCompiler {
    public CompilerECJ() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<IdentifiedBytecode> compileClass(
            ClassesProvider classprovider,
            Optional<MessagesListener> messagesConsummer,
            IdentifiedSource... javaSourceFiles) {
        throw new UnsupportedOperationException();
    }
}
