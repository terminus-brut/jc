package io.github.mkoncek.classpathless.api;

import java.text.MessageFormat;

public interface MessagesListener {
    /**
     * Allows InMemoryCompiler to send runtime updates to caller.
     * @param level Severity of information.
     * @param message The message.
     */
    void addMessage(java.util.logging.Level level, String message);

    /**
     * Allows InMemoryCompiler to send runtime updates to caller.
     * @param level Severity of information.
     * @param format Format string as given to MessageFormat.
     * @param args Arguments to format.
     */
    default void addMessage(java.util.logging.Level level, String format, Object... args) {
        addMessage(level, MessageFormat.format(format, args));
    }
}
