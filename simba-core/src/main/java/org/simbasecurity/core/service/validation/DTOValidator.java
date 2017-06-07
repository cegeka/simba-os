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

public class DTOValidator {
    private static final HTMLValidationRule hvr = new SimbaHTMLValidationRule( "safehtml", ESAPI.encoder() );

    public static void assertValidString(String methodName, String value) throws ValidationException {
        if(value!=null && !StringUtils.isBlank(value)) {
            hvr.setValidateInputAndCanonical(false);
            hvr.assertValid(methodName, value);
        }
    }

}
