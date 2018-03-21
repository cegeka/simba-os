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
package org.simbasecurity.core.jaas.loginmodule;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.simbasecurity.core.service.CredentialService;

import javax.security.auth.login.FailedLoginException;

/**
 * Legacy support
 */
public class FallbackDatabaseLoginModule extends DatabaseLoginModule {

    @Override
    protected boolean verifyLoginData() throws FailedLoginException {
        debug("Verifying credentials for user: " + getUsername());

        boolean validCredentials;
        try {
            validCredentials = credentialService.checkCredentials(getUsername(), getPassword());
        } catch (EncryptionOperationNotPossibleException legacyPasswordException) {
            validCredentials = verifyWithSHA1Encryptor(credentialService);
        }

        if (validCredentials) {
            debug("Authentication succeeded");
            return true;
        }

        debug("Authentication failed");
        throw new FailedLoginException(getUsername());
    }

    private boolean verifyWithSHA1Encryptor(CredentialService credentialService) {
        return credentialService.checkCredentialsWithSHA1EncryptorAndReEncrypt(getUsername(), getPassword());
    }

}