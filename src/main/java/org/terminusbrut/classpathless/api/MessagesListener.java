package org.terminusbrut.classpathless.api;

public interface MessagesListener {
    /**
     * Allows InMemoryCompiler to send runtime updates to caller
     * @param level severity of information
     * @param message the message
     */
    void addMessage(java.util.logging.Level level, String message);
}
