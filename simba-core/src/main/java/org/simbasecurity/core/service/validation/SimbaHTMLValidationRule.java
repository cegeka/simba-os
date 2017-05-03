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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.ConfigurationException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.HTMLValidationRule;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

@SuppressWarnings("deprecation")
public class SimbaHTMLValidationRule extends HTMLValidationRule {

    protected static Policy antiSamyPolicy = null;

    static {
        InputStream resourceStream;
        try {
            resourceStream = ESAPI.securityConfiguration().getResourceStream("antisamy-esapi.xml");
        } catch (IOException e) {
            throw new ConfigurationException("Couldn't find antisamy-esapi.xml", e);
        }
        if (resourceStream != null) {
            try {
                antiSamyPolicy = Policy.getInstance(resourceStream);
            } catch (PolicyException e) {
                throw new ConfigurationException("Couldn't parse antisamy policy", e);
            }
        }
    }
    
    public SimbaHTMLValidationRule(String typeName, Encoder encoder) {
        super(typeName, encoder);
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public boolean isValid(String context, String input) {
        String canonical = encoder.canonicalize(input);
        try {
            AntiSamy as = new AntiSamy();
            CleanResults test = as.scan(canonical, antiSamyPolicy);

            List<String> errors = test.getErrorMessages();
            if ( !errors.isEmpty() ) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void assertValid(String context, String input) throws ValidationException {
        if(!isValid(context, input)) {
            throw new IllegalArgumentException("The value "+input+" is not valid for "+context);
        }
    }
}
