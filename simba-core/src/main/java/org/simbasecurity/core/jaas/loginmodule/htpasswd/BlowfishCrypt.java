package org.simbasecurity.core.jaas.loginmodule.htpasswd;

import org.simbasecurity.core.jaas.loginmodule.HtPasswdLoginModule;

public class BlowfishCrypt implements HtPasswdCrypt {
    @Override
    public boolean accepts(HtPasswdLoginModule module, String hash) {
        return hash != null && hash.startsWith("$2y$");
    }

    @Override
    public boolean checkPassword(HtPasswdLoginModule module, String hash, String plainTextPassword) {
        module.debug("BlowFish format not yet supported");
        return false;
    }
}
