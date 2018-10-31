package com.github.lucapino.confluence.model;

/**
 * Represents the storage container within a {@code Content.body}
 *
 * @author Jonathon Hope
 * @see
 * <a href="https://confluence.atlassian.com/display/DOC/Confluence+Storage+Format">
 * Confluence Storage Format</a>
 */
public class Storage {

    /**
     * @see
     * <a href="https://confluence.atlassian.com/display/DOC/Confluence+Storage+Format">
     * Confluence Storage Format</a>
     */
    public enum Representation {
        VIEW,
        PAGE,
        STORAGE,
        WIKI;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
