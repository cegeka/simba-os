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

package org.simbasecurity.core.service.validation;

import junit.framework.TestCase;
import org.simbasecurity.core.service.manager.dto.UserDTO;

public class DTOValidatorTest extends TestCase {

    public void testAssertValid_throwsExceptionOnJavascript() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setFirstName("<script>alert('hello')</script>John");

        try {
            DTOValidator.assertValid(dto);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {}
    }

    public void testAssertValid_Valid() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setFirstName("John");
        dto.setName("L\u00e9");
        dto.setUserName("L&eacute;");

        DTOValidator.assertValid(dto);

        assertEquals("John", dto.getFirstName());
        assertEquals("L\u00e9", dto.getName());
        assertEquals("L&eacute;", dto.getUserName());
    }

    public void testEncodeForHTML_EncodesSpecialCharacters() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setFirstName("<b>John</b>");
        dto.setName("L\u00e9");

        DTOValidator.encodeForHTML(dto);

        assertEquals("&lt;b&gt;John&lt;&#x2f;b&gt;", dto.getFirstName());
        assertEquals("L&eacute;", dto.getName());
    }
}
