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

import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.HTMLValidationRule;
import org.simbasecurity.core.service.manager.dto.AbstractIdentifiableDTO;

import java.lang.reflect.Method;

public class DTOValidator {
    private static final HTMLValidationRule hvr = new SimbaHTMLValidationRule( "safehtml", ESAPI.encoder() );

    public static void assertValid(AbstractIdentifiableDTO dto) {
        Method[] declaredMethods = dto.getClass().getDeclaredMethods();
        for(Method method : declaredMethods) {
            if(method.getName().startsWith("get") && method.getReturnType().isAssignableFrom(String.class)) {
                assertValidField(dto, method.getName().substring(3, method.getName().length()));
            }
        }
    }

    private static void assertValidField(AbstractIdentifiableDTO dto, String methodName) {
        try {
            String value = (String) dto.getClass().getMethod("get"+methodName).invoke(dto);
            assertValidString(methodName, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to access get/set "+methodName+" on "+dto.getClass().getName(), e);
        }
    }

    public static void assertValidString(String methodName, String value) throws ValidationException {
        if(value!=null && !StringUtils.isBlank(value)) {
            hvr.setValidateInputAndCanonical(false);
            hvr.assertValid(methodName, value);
        }
    }

    public static void encodeForHTML(AbstractIdentifiableDTO dto) {
        Method[] declaredMethods = dto.getClass().getDeclaredMethods();
        for(Method method : declaredMethods) {
            if(method.getName().startsWith("get") && method.getReturnType().isAssignableFrom(String.class)) {
                encodeFieldForHTML(dto, method.getName().substring(3, method.getName().length()));
            }
        }
    }

    private static void encodeFieldForHTML(AbstractIdentifiableDTO dto, String methodName) {
        try {
            String value = (String) dto.getClass().getMethod("get"+methodName).invoke(dto);
            if(value!=null && !StringUtils.isBlank(value)) {
                String cleanedValue = ESAPI.encoder().encodeForHTML(value);
                dto.getClass().getMethod("set"+methodName, new Class[]{String.class}).invoke(dto, cleanedValue);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to access get/set "+methodName+" on "+dto.getClass().getName(), e);
        }
    }

}
