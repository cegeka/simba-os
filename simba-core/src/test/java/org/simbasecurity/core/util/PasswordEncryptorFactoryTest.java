/*
 * Copyright 2013 Simba Open Source
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

import static org.junit.Assert.*;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.junit.Test;

public class PasswordEncryptorFactoryTest {

    @Test
    public void configIsValid() throws Exception {

        PasswordEncryptorFactory factory = new PasswordEncryptorFactory();
        ConfigurablePasswordEncryptor encryptor = (ConfigurablePasswordEncryptor) factory.makeObject();
        assertNotNull(encryptor);

        String encryptedPassword = encryptor.encryptPassword("Simba3D");

        boolean passwordValid = encryptor.checkPassword("Simba3D", encryptedPassword);
        assertTrue(passwordValid);

        boolean passwordIsValid = encryptor.checkPassword("Simbadummy", encryptedPassword);
        assertFalse(passwordIsValid);
    }

}