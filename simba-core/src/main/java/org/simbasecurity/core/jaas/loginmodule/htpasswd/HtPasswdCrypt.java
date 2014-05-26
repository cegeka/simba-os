package org.simbasecurity.core.jaas.loginmodule.htpasswd;


import org.simbasecurity.core.jaas.loginmodule.HtPasswdLoginModule;

public interface HtPasswdCrypt {
    boolean accepts(HtPasswdLoginModule module, String hash);
    boolean checkPassword(HtPasswdLoginModule module, String hash, String plainTextPassword);
}
