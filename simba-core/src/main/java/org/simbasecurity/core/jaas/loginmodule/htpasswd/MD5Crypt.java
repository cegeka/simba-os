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

import java.security.MessageDigest;

import org.simbasecurity.core.jaas.loginmodule.HtPasswdLoginModule;

public class MD5Crypt implements HtPasswdCrypt {

    private static final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public boolean accepts(HtPasswdLoginModule module, String hash) {
        return hash != null && hash.startsWith("$apr1$");
    }

    @Override
    public boolean checkPassword(HtPasswdLoginModule module, String hash, String plainTextPassword) {
        return hash.equals(crypt(plainTextPassword, hash));
    }

    private String crypt(String password, String salt) {
        return crypt(password, salt, "$apr1$");
    }

    public final String crypt(String password, String salt, String magic) {
        // This string is magic for this algorithm.  Having it this way, we can get get better later on
        byte finalState[];
        MessageDigest ctx, ctx1;
        long l;

        /* -- */
        /* Refine the Salt first */
        /* If it starts with the magic string, then skip that */

        if (salt.startsWith(magic)) {
            salt = salt.substring(magic.length());
        }

        /* It stops at the first '$', max 8 chars */
        if (salt.indexOf('$') != -1) {
            salt = salt.substring(0, salt.indexOf('$'));
        }

        if (salt.length() > 8) {
            salt = salt.substring(0, 8);
        }

        ctx = getMD5();

        ctx.update(password.getBytes());    // The password first, since that is what is most unknown
        ctx.update(magic.getBytes());       // Then our magic string
        ctx.update(salt.getBytes());        // Then the raw salt

        /* Then just as many characters of the MD5(pw,salt,pw) */
        ctx1 = getMD5();
        ctx1.update(password.getBytes());
        ctx1.update(salt.getBytes());
        ctx1.update(password.getBytes());
        finalState = ctx1.digest();

        for (int pl = password.length(); pl > 0; pl -= 16) {
            ctx.update(finalState, 0, pl > 16 ? 16 : pl);
        }

        /* the original code claimed that finalState was being cleared
           to keep dangerous bits out of memory, but doing this is also
           required in order to get the right output. */
        clearBits(finalState);

        /* Then something really weird... */
        for (int i = password.length(); i != 0; i >>>= 1) {
            if ((i & 1) != 0) {
                ctx.update(finalState, 0, 1);
            } else {
                ctx.update(password.getBytes(), 0, 1);
            }
        }

        finalState = ctx.digest();

        // Loop 1000 times as stated by the spec
        for (int i = 0; i < 1000; i++) {
            ctx1.reset();

            if ((i & 1) != 0) {
                ctx1.update(password.getBytes());
            } else {
                ctx1.update(finalState, 0, 16);
            }

            if ((i % 3) != 0) {
                ctx1.update(salt.getBytes());
            }

            if ((i % 7) != 0) {
                ctx1.update(password.getBytes());
            }

            if ((i & 1) != 0) {
                ctx1.update(finalState, 0, 16);
            } else {
                ctx1.update(password.getBytes());
            }

            finalState = ctx1.digest();
        }

        /* Now make the output string */
        StringBuilder result = new StringBuilder();
        result.append(magic);
        result.append(salt);
        result.append("$");

        l = (bytes2u(finalState[0]) << 16) | (bytes2u(finalState[6]) << 8) | bytes2u(finalState[12]);
        result.append(to64(l, 4));

        l = (bytes2u(finalState[1]) << 16) | (bytes2u(finalState[7]) << 8) | bytes2u(finalState[13]);
        result.append(to64(l, 4));

        l = (bytes2u(finalState[2]) << 16) | (bytes2u(finalState[8]) << 8) | bytes2u(finalState[14]);
        result.append(to64(l, 4));

        l = (bytes2u(finalState[3]) << 16) | (bytes2u(finalState[9]) << 8) | bytes2u(finalState[15]);
        result.append(to64(l, 4));

        l = (bytes2u(finalState[4]) << 16) | (bytes2u(finalState[10]) << 8) | bytes2u(finalState[5]);
        result.append(to64(l, 4));

        l = bytes2u(finalState[11]);
        result.append(to64(l, 2));

        /* Don't leave anything around in vm they could use. */
        clearBits(finalState);

        return result.toString();
    }

    private int bytes2u(byte inp) {
        return (int) inp & 0xff;
    }

    private String to64(long v, int size) {
        StringBuilder result = new StringBuilder();

        while (--size >= 0) {
            result.append(itoa64.charAt((int) (v & 0x3f)));
            v >>>= 6;
        }

        return result.toString();
    }

    private void clearBits(byte bits[]) {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = 0;
        }
    }

    private MessageDigest getMD5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
