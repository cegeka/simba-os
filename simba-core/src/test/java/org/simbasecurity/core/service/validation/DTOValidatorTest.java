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
