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
package com.github.lucapino.confluence.util;

import com.google.common.collect.Iterables;

import java.util.Arrays;

/**
 * A collection of static {@code String} utilities.
 *
 * @author Jonathon Hope
 */
public final class StringUtils {

    /**
     * Converts the {@code String str}, to {@code "camelCase"} format.
     *
     * @param str the {@code String} to format.
     * @return the formatted {@code String}.
     */
    public static String convertToCamelCase(final String str) {
        final String[] parts = str.split("_");
        if (parts.length > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(parts[0].toLowerCase());
            for (String s : Iterables.skip(Arrays.asList(parts), 1)) {
                sb.append(Character.toUpperCase(s.charAt(0)));
                if (s.length() > 1) {
                    sb.append(s.substring(1, s.length()).toLowerCase());
                }
            }
            return sb.toString();
        }
        return str;
    }

    /**
     * Converts {@literal "UpperCamelCase"}.
     * <pre>{@literal
     *     "RENDER_MODE" => "RenderMode"
     * }</pre>
     *
     * @param str the String to convert. The Capital letters
     *            are chosen by the positions of {@code '_'}
     *            in the {@code String}.
     * @return a String in Upper Camel Case format.
     */
    public static String convertToUpperCamel(final String str) {
        StringBuilder sb = new StringBuilder();
        for (String s : str.split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1, s.length()).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * Convert to {@literal "Proper case"}; capital first letter, lowercase suffix.
     *
     * @param s the {@code String} to convert to {@literal "ProperCase"}.
     * @return the {@code String s} converted to {@literal "ProperCase"}.
     */
    public static String toProperCase(final String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }


}
