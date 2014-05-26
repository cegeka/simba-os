package org.simbasecurity.core.jaas.loginmodule.htpasswd;

import org.simbasecurity.core.jaas.loginmodule.HtPasswdLoginModule;

public class PlainTextCrypt implements HtPasswdCrypt {
    @Override
    public boolean accepts(HtPasswdLoginModule module, String hash) {
        return hash != null;
    }

    @Override
    public boolean checkPassword(HtPasswdLoginModule module, String hash, String plainTextPassword) {
        return hash.equals(plainTextPassword);
    }
}
