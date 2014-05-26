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
package org.simbasecurity.core.jaas.loginmodule.htpasswd;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.simbasecurity.core.jaas.loginmodule.HtPasswdLoginModule;

@RunWith(MockitoJUnitRunner.class)
public class MD5CryptTest  {

    private static final String SAMPLE_HASH = "$apr1$q5fpwm63$5qK4aaNGxPKt7qGl/GzWB/";
    private static final String SAMPLE_PASSWORD = "123P6ssw0rd!";

    @Mock
    private HtPasswdLoginModule module;

    private MD5Crypt crypt;

    @Before
    public void setUp() throws Exception {
        crypt = new MD5Crypt();
    }

    @Test
    public void testAccepts() throws Exception {
        assertTrue(crypt.accepts(module, SAMPLE_HASH));
    }

    @Test
    public void testCheckPassword() throws Exception {
        assertTrue(crypt.checkPassword(module, SAMPLE_HASH, SAMPLE_PASSWORD));

        assertFalse(crypt.checkPassword(module, SAMPLE_HASH, "wrongpassword"));
    }
}
