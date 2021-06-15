package org.terminusbrut.classpathless.api;

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
    void addMessage(java.util.logging.Level level, String format, Object... args);
}
