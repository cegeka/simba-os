package org.simbasecurity.core.domain.repository.communication.token;

import org.simbasecurity.core.domain.communication.token.Token;
import org.simbasecurity.core.domain.communication.token.UserToken;
import org.simbasecurity.core.domain.repository.AbstractVersionedDatabaseRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class UserTokenRepository extends AbstractVersionedDatabaseRepository<UserToken> {

    @Override
    protected Class<UserToken> getEntityType() {
        return UserToken.class;
    }

    public Optional<UserToken> findByUserId(long userId) {
        TypedQuery<UserToken> query = entityManager.createQuery("SELECT ut FROM UserToken ut WHERE ut.userId = :userId", UserToken.class)
                .setParameter("userId", userId);

        return query.getResultList().stream().findFirst();
    }

    public Optional<UserToken> findByToken(Token token) {
        TypedQuery<UserToken> query = entityManager.createQuery("SELECT ut FROM UserToken ut WHERE ut.token = :token", UserToken.class)
                .setParameter("token", token);

        return query.getResultList().stream().findFirst();
    }

    public void deleteToken(Token token) {
        Query query = entityManager.createQuery("DELETE FROM UserToken ut WHERE ut.token = :token");
        query.setParameter("token", token);
        query.executeUpdate();
    }
}
