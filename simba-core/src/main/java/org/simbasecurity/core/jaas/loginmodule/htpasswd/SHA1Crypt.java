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
