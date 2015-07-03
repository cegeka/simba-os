package org.simbasecurity.core.chain.eid;

public class SAMLUser {
    private final String insz;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String language;

    public SAMLUser(String insz, String firstname, String lastname, String email, String language) {
        this.insz = insz;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.language = language;
    }

    public String getInsz() {
        return insz;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getLanguage() {
        return language;
    }
}
