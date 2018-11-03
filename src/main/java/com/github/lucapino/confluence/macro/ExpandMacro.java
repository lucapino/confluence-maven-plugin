/*
 * Copyright 2013 Luca Tagliani
 * Copyright 2015 Jonathon Hope
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
package com.github.lucapino.confluence.macro;

/**
 * Represents an expandable body of rich text.
 *
 * @author Jonathon Hope
 * @see <a href="https://confluence.atlassian.com/display/DOC/Expand+Macro"> Expand Macro docs </a>
 */
public class ExpandMacro {

    /**
     * The title of the expandable.
     */
    private final String title;
    /**
     * The contents of the expandable.
     */
    private final String body;

    /**
     * @param builder the builder factory to use.
     */
    protected ExpandMacro(final Builder builder) {
        this.title = builder.title;
        this.body = builder.body;
    }

    /**
     * @return converts this instance to confluence markup.
     */
    public String toMarkup() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ac:structured-macro ac:name=\"expand\">");
        sb.append("<ac:parameter ac:name=\"title\">");
        sb.append(title);
        sb.append("</ac:parameter>");

        sb.append("<ac:rich-text-body>");
        sb.append(body);
        sb.append("</ac:rich-text-body>");
        sb.append("</ac:structured-macro>");
        return sb.toString();
    }

    /**
     * Builder factory method.
     *
     * @return a {@code Builder} instance for chain-building a {@code ExpandMacro}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A class for implementing the Builder Pattern for {@code ExpandMacro}.
     */
    public static class Builder {

        private String title;
        private String body;

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public ExpandMacro build() {
            return new ExpandMacro(this);
        }

    }

}
