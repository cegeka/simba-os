package org.simbasecurity.core.domain.communication.token;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class Token {
    @Column(name = "TOKEN", unique = true)
    private String internalRepresentation;

    protected Token(){}

    private Token(String internalRepresentation) {
        this.internalRepresentation = internalRepresentation;
    }

    public static Token generateToken() {
        return new Token(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;

        Token token = (Token) o;

        return internalRepresentation != null ? internalRepresentation.equals(token.internalRepresentation) : token.internalRepresentation == null;
    }

    @Override
    public int hashCode() {
        return internalRepresentation != null ? internalRepresentation.hashCode() : 0;
    }
}
