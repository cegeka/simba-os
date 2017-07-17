package org.simbasecurity.core.service.thrift;

public class ThriftTokenAccess {

    private static ThreadLocal<String> threadLocalSsoToken = new ThreadLocal<>();

    public static void set(String ssoToken) {
        threadLocalSsoToken.set(ssoToken);
    }

    public static String get() {
        return threadLocalSsoToken.get();
    }

    static void clean() {
        threadLocalSsoToken.remove();
    }
}
