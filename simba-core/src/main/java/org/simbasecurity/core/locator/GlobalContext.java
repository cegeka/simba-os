/*
 * Copyright 2011 Simba Open Source
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
package org.simbasecurity.core.locator;


public final class GlobalContext {


    private static Locator locator = new SpringAwareLocator();

    public static void initialize(Locator singleton) {
        locator = singleton;
    }

    public static <B> B locate(Class<B> type) {
        return locator.locate(type);
    }

    public static Object locate(String naam) {
        return locator.locate(naam);
    }

    public static <B> B locate(Class<B> type, String name) {
        return locator.locate(name, type);
    }

    public static <B> B locate(Class<B> type, Enum<?> enumeration) {
        return locator.locate(getBeanNameForEnum(enumeration), type);
    }

    public static boolean isLocatable(Enum<?> enumeration) {
        return isLocatable(getBeanNameForEnum(enumeration));
    }

    public static boolean isLocatable(String name) {
        return locator.locatable(name);
    }

    private static String getBeanNameForEnum(Enum<?> enumeration) {
        return enumeration.getClass().getSimpleName() + "." + enumeration.name();
    }

    private GlobalContext() {
    }

    public static void destroy() {
        locator.destroy();
    }

}
