/*
 * Copyright 2013-2017 Simba Open Source
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
 *
 */
package org.simbasecurity.common.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public final class StringUtilTest {

    @Test
    public void testPrivateConstructor() throws InvocationTargetException, IllegalAccessException,
    InstantiationException {
        final Constructor<?>[] constructors = StringUtil.class.getDeclaredConstructors();
        constructors[0].setAccessible(true);
        final StringUtil actual = (StringUtil) constructors[0].newInstance();
        assertNotNull(actual);
    }

    @Test
    public void nullStringIsEmpty() {
        final boolean actual = StringUtil.isEmpty(null);
        assertTrue(actual);

    }

    @Test
    public void emptyStringIsEmpty() {
        final boolean actual = StringUtil.isEmpty("");
        assertTrue(actual);

    }

    @Test
    public void nonEmptyStringIsNotEmpty() {
        final boolean actual = StringUtil.isEmpty("abc");
        assertFalse(actual);

    }

    @Test
    public void substringReturnsSubstring() {
        final String actual = StringUtil.substringAfter("aaa|bbb", "|");
        assertEquals("bbb", actual);
    }

    @Test
    public void substringDoesNotFindSubstring() {
        final String actual = StringUtil.substringAfter("aaabbbccc", "notfound");
        assertEquals("", actual);
    }

    @Test
    public void substringWithNullSeparatorReturnsEmptyString() {
        final String actual = StringUtil.substringAfter("aaa|bbb", null);
        assertEquals("", actual);
    }

    @Test
    public void substringOfNullStringIsNull() {
        final String actual = StringUtil.substringAfter(null, "|");
        assertNull(actual);
    }

    @Test
    public void substringOfEmptyStringIsEmpty() {
        final String actual = StringUtil.substringAfter("", "|");
        assertEquals("", actual);
    }
}
