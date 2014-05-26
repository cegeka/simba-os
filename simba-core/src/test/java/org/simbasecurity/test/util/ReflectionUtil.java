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
package org.simbasecurity.test.util;

import java.lang.reflect.Field;

public final class ReflectionUtil {

    public static void setField(Object object, String fieldName, Object value) {
        try {
            getField(object.getClass(), fieldName).set(object, value);
        } catch (Exception unexpectedReflectionException) {
            throw new RuntimeException("unexpected exception ", unexpectedReflectionException);
        }
    }

    public static void setField(Object object, String fieldName, int value) {
        try {
            getField(object.getClass(), fieldName).setInt(object, value);
        } catch (Exception unexpectedReflectionException) {
            throw new RuntimeException("unexpected exception ", unexpectedReflectionException);
        }
    }

    public static void setField(Object object, String fieldName, long value) {
        try {
            getField(object.getClass(), fieldName).setLong(object, value);
        } catch (Exception unexpectedReflectionException) {
            throw new RuntimeException("unexpected exception ", unexpectedReflectionException);
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            if (clazz != null) {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            }
        } catch (NoSuchFieldException ex) {
            return getField(clazz.getSuperclass(), fieldName);
        }
        throw new RuntimeException("No such field " + fieldName);
    }
}
