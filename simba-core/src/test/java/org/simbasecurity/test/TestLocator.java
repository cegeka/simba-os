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
package org.simbasecurity.test;

import static org.mockito.Mockito.*;

import org.simbasecurity.core.locator.GlobalContext;
import org.simbasecurity.core.locator.Locator;

public class TestLocator {

    public static Locator createLocatorMock() {
        Locator locator = mock(Locator.class);
        GlobalContext.initialize(locator);
        return locator;
    }

    public static <T> T implant(Locator locator, Class<T> aClass, T instance) {
        when(locator.locate(aClass)).thenReturn(instance);
        return instance;
    }

    public static <T> T implantMock(Locator locator, Class<T> classToMock) {
        T mock = mock(classToMock);
        when(locator.locate(classToMock)).thenReturn(mock);
        return mock;
    }

    public static <T> T implantMockLocatingByNameOnly(Locator locator, Class<T> classToMock, String name) {
        T mock = mock(classToMock);
        when(locator.locate(name)).thenReturn(mock);
        return mock;
    }

    private TestLocator() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate a static utility class");
    }

}