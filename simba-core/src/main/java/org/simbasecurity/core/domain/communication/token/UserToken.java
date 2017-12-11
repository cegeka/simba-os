package org.simbasecurity.core.domain.communication.token;

import org.hibernate.annotations.Type;
import org.simbasecurity.core.domain.AbstractVersionedEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SIMBA_USER_TOKEN")
@DiscriminatorColumn(name = "CLASS", discriminatorType = DiscriminatorType.STRING)
public abstract class UserToken extends AbstractVersionedEntity {

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_USER_TOKEN", allocationSize = 1)
    protected long id = 0;

    @Embedded
    private Token token;

    @Column(name = "SIMBA_USER_ID", unique = true)
    private long userId;

    @Column(name = "EXPIRES_ON")
    @Type(type="org.simbasecurity.core.util.hibernate.LocalDateTimeUserType")
    private LocalDateTime expiresOn;

    protected UserToken(){}

    UserToken(Token token, long userId, LocalDateTime expiresOn) {
        this.token = token;
        this.userId = userId;
        this.expiresOn = expiresOn;
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

    public LocalDateTime getExpiresOn() {
        return expiresOn;
    }

    public long getUserId() {
        return userId;
    }
}
