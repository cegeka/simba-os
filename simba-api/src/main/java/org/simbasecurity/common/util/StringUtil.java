/*
 * Copyright 2013 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.simbasecurity.common.util;

public final class StringUtil {

    public static final String EMPTY = "";

    private StringUtil() {
        // utility class should not be instantiated
    }

    public static boolean isEmpty(final CharSequence input) {
        return input == null || input.length() == 0;
    }

    public static String substringAfter(final String input, final String separator) {
        if (isEmpty(input)) {
            return input;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = input.indexOf(separator);
        if (pos == -1) {
            return EMPTY;
        }
        return input.substring(pos + separator.length());
    }
}
