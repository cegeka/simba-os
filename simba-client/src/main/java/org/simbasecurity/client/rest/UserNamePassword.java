package org.simbasecurity.client.rest;

import javax.xml.bind.DatatypeConverter;

public class UserNamePassword {
    private final String userName;
    private final String password;

    private UserNamePassword(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public static UserNamePassword fromBasicHeader(String auth) {
        if(!auth.toLowerCase().startsWith("basic ")) {
            throw new UnsupportedOperationException("Only Basic Authentication supported so far");
        }
        String[] split = new String(decode(auth.substring(6))).split(":", 2);
        return fromString(split);
    }

    private static byte[] decode(String substring) {
        try {
            return DatatypeConverter.parseBase64Binary(substring);
        } catch(Exception e){
            throw new UnsupportedOperationException(String.format("Not a valid base64 encoded header: %s", substring));
        }
    }

    private static UserNamePassword fromString(String[] split) {
        if(split.length != 2){
            throw new UnsupportedOperationException("Not a valid basic authentication");
        }
        return new UserNamePassword(split[0], split[1]);
    }
}
