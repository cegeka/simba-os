package org.simbasecurity.core.domain.communication.token;

import org.simbasecurity.core.domain.AbstractVersionedEntity;

import javax.persistence.*;

@Entity
@Table(name = "SIMBA_USER_TOKEN")
public class UserToken extends AbstractVersionedEntity {

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_USER_TOKEN", allocationSize = 1)
    protected long id = 0;

    @Embedded
    private Token token;

    @Column(name = "SIMBA_USER_ID", unique = true)
    private long userId;

    protected UserToken() { }

    private UserToken(Token token, long userId) {
        this.token = token;
        this.userId = userId;
    }

    public static UserToken userToken(Token token, long userId) {
        return new UserToken(token, userId);
    }

    @Override
    public long getId() {
        return id;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
