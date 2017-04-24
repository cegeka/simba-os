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

package org.simbasecurity.core.jaas.loginmodule.htpasswd;

import org.simbasecurity.core.jaas.loginmodule.HtPasswdLoginModule;

public class SHA1Crypt implements HtPasswdCrypt {
    @Override
    public boolean accepts(HtPasswdLoginModule module, String hash) {
        return hash != null && hash.startsWith("{SHA}");
    }

    @Override
    public boolean checkPassword(HtPasswdLoginModule module, String hash, String plainTextPassword) {
        module.debug("SHA1 format not yet supported");
        return false;
    }
}
