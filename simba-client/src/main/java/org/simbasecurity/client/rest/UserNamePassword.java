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

    public static UserNamePassword fromBasicAuthenticationHeader(String auth) {
        if(auth == null){
            throw new UnsupportedOperationException("An authorization header is required");
        }
        if(!auth.toLowerCase().startsWith("basic ")) {
            throw new UnsupportedOperationException(String.format("'%s' is not a correct basic authentication header", auth));
        }
        String[] split = new String(decode(auth.substring(6))).split(":");
        return fromString(split);
    }

    private static byte[] decode(String substring) {
        try {
            return DatatypeConverter.parseBase64Binary(substring);
        } catch(Exception e){
            throw new UnsupportedOperationException(String.format("'%s' is not a valid base64 encoded string", substring));
        }
    }

    private static UserNamePassword fromString(String[] split) {
        if(split.length != 2){
            throw new UnsupportedOperationException("The provided authorization needs to be in the form 'username:password'");
        }
        return new UserNamePassword(split[0].trim(), split[1].trim());
    }
}
