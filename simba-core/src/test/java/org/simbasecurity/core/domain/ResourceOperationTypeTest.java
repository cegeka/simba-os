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
package org.simbasecurity.core.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceOperationTypeTest {

    @Test
    public void testResolve() {
        assertEquals(ResourceOperationType.CREATE, ResourceOperationType.resolve("create"));
        assertEquals(ResourceOperationType.CREATE, ResourceOperationType.resolve("CREATE"));
        assertEquals(ResourceOperationType.DELETE, ResourceOperationType.resolve("delete"));
        assertEquals(ResourceOperationType.READ, ResourceOperationType.resolve("read"));
        assertEquals(ResourceOperationType.WRITE, ResourceOperationType.resolve("write"));

        assertEquals(ResourceOperationType.UNKNOWN, ResourceOperationType.resolve("unknown"));
        assertEquals(ResourceOperationType.UNKNOWN, ResourceOperationType.resolve("illegal"));
        assertEquals(ResourceOperationType.UNKNOWN, ResourceOperationType.resolve(null));
    }

}

