package org.jc.api;



public interface MessagesListener {

    /**
     * Allows InMemoryCompiler to send runtime updates to caller
     * @param level seerity of information
     * @param message the message
     */
    void addMessage(java.util.logging.Level level, String message);
}
