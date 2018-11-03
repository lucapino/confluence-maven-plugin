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
 * The HTML macro allows you to add HTML code to a Confluence page.
 *
 * @author Jonathon Hope
 * @see <a href="https://confluence.atlassian.com/display/DOC/HTML+Macro">HTML Macro Documentation</a>
 */
public class HtmlMacro {
    /**
     * The HTML source.
     */
    private final String content;

    /**
     * Constructor.
     *
     * @param content the HTML source.
     *                NOTE: This cannot contain a {@literal <DOCTYPE html>} tag.
     */
    public HtmlMacro(final String content) {
        this.content = content;
    }

    /**
     * @return a structured macro (XML formatted) String, according to the confluence HTML Macro documentation.
     */
    public String toMarkup() {
        final StringBuilder sb = new StringBuilder(content.length() + 140);
        sb.append("<ac:structured-macro ac:name=\"html\">");
        sb.append("<ac:plain-text-body>");
        sb.append("<![CDATA[");
        sb.append(content);
        sb.append("]]");
        sb.append("</ac:plain-text-body>");
        sb.append("</ac:structured-macro>");

        return sb.toString();
    }

}
