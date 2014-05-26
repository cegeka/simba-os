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
package org.simbasecurity.client.authorization.caching;

import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

public class SoftHashMapTest {

    private static final String KEY_1 = "key-1";
    private static final String KEY_2 = "key-2";
    private static final String KEY_3 = "key-3";

    private static final String VALUE_1 = "value-1";
    private static final String VALUE_2 = "value-2";
    private static final String VALUE_3 = "value-3";

    private SoftHashMap<String, String> testedMap = new SoftHashMap<String, String>();

    @Test
    public void testSize() {
        assertEquals(0, testedMap.size());
        testedMap.put(KEY_1, VALUE_1);
        assertEquals(1, testedMap.size());

        testedMap.put(KEY_2, VALUE_2);
        assertEquals(2, testedMap.size());

        testedMap.put(KEY_3, VALUE_3);
        assertEquals(3, testedMap.size());

        testedMap.remove(KEY_1);
        assertEquals(2, testedMap.size());
    }

    @Test
    public void testClear() {
        testedMap.put(KEY_1, VALUE_1);
        testedMap.put(KEY_2, VALUE_2);
        testedMap.put(KEY_3, VALUE_3);

        testedMap.clear();
        assertEquals(0, testedMap.size());
        assertNull(testedMap.get(KEY_1));
    }

    @Test
    public void testGetObject() {
        testedMap.put(KEY_1, VALUE_1);
        testedMap.put(KEY_2, VALUE_2);
        testedMap.put(KEY_3, VALUE_3);

        assertEquals(VALUE_1, testedMap.get(KEY_1));
        assertEquals(VALUE_2, testedMap.get(KEY_2));
        assertEquals(VALUE_3, testedMap.get(KEY_3));
    }

    @Test
    public void testPutKV() {
        assertNull(testedMap.put(KEY_1, VALUE_1));
        assertNull(testedMap.put(KEY_2, VALUE_2));
        assertNull(testedMap.put(KEY_3, VALUE_3));

        assertEquals(VALUE_1, testedMap.put(KEY_1, VALUE_2));
    }

    @Test
    public void testRemoveObject() {
        testedMap.put(KEY_1, VALUE_1);
        testedMap.put(KEY_2, VALUE_2);
        testedMap.put(KEY_3, VALUE_3);

        assertEquals(VALUE_1, testedMap.remove(KEY_1));
        assertNull(testedMap.remove(KEY_1));
    }

    @Test
    public void testEntrySet() {
        testedMap.put(KEY_1, VALUE_1);
        testedMap.put(KEY_2, VALUE_2);

        Set<Entry<String, String>> entrySet = testedMap.entrySet();

        assertEquals(2, entrySet.size());

        for (Entry<String, String> entry : entrySet) {
            if (KEY_1.equals(entry.getKey())) {
                entry.setValue(VALUE_3);
            }
        }

        assertEquals(VALUE_3, testedMap.get(KEY_1));
    }

}
