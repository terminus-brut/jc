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

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.mkoncek.classpathless.api.MessagesListener;

public class LoggingSwitch {
    public static class NullMessagesListener implements MessagesListener {
        @Override
        public void addMessage(Level level, String message) {
        }

        @Override
        public void addMessage(Level level, String format, Object... args) {
        }
    };

    private PrintStream printer;
    private MessagesListener compilerMessagesListener;
    private boolean tracing = false;
    private java.util.logging.Level logLevel = Level.OFF;

    public LoggingSwitch(PrintStream printer, MessagesListener compilerMessagesListener) {
        this.printer = printer;
        this.compilerMessagesListener = compilerMessagesListener;
    }

    public void setTracing(boolean value) {
        this.tracing = value;
    }

    public void setLogLevel(java.util.logging.Level value) {
        this.logLevel = value;
    }

    public void trace(Object struct, String name, Object... args) {
        if (tracing) {
            logln(Level.FINEST, "[TRACE] invoking {0}::{1}({2})", struct.getClass().getName(), name,
                    Stream.of(args).map(arg -> arg == null ? "<null>" : arg.toString())
                    .collect(Collectors.joining(", ")));
        }
    }

    public void trace(Object result) {
        if (tracing) {
            logln(Level.FINEST, "[TRACE] returning {0}", result == null ? "<null>" : result.toString());
        }
    }

    public void log(java.util.logging.Level level, String format, Object... args) {
        if (logLevel != Level.OFF && level.intValue() >= logLevel.intValue()) {
            var message = "[LOG] " + MessageFormat.format(format, args);
            printer.print(message);
            if (level == Level.SEVERE) {
                compilerMessagesListener.addMessage(level, message);
            }
        }
    }

    public void logln(java.util.logging.Level level, String format, Object... args) {
        log(level, format + System.lineSeparator(), args);
    }
}
