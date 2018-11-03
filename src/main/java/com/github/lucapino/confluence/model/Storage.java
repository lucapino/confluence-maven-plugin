/*
 * Copyright 2011 Tomasz Maciejewski
 * Copyright 2013 Luca Tagliani
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
