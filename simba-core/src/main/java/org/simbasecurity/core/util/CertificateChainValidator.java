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
package org.simbasecurity.core.util;

import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.util.List;

import be.fedict.trust.BelgianTrustValidatorFactory;
import be.fedict.trust.TrustValidator;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateChainValidator {

    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory eventFactory;

    public void validate(List<X509Certificate> certificateChain, String userName, String clientIpAddress) {
        TrustValidator trustValidator = BelgianTrustValidatorFactory.createTrustValidator();
        try {
            trustValidator.isTrusted(certificateChain);
        } catch (CertPathValidatorException e) {
        	audit.log(eventFactory.createEventForAuthenticationEID(userName, clientIpAddress, "E-ID Certificate was not trusted"));
            throw new SecurityException("Certificate was not trusted. Message: " + e.getMessage());
        }
    }

}