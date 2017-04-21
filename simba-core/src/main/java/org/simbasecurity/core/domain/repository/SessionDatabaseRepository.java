/*
 * Copyright 2011 Simba Open Source
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
 */
package org.simbasecurity.core.domain.repository;

import java.util.Collection;
import java.util.List;
import javax.persistence.Query;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.Session;
import org.simbasecurity.core.domain.SessionEntity;
import org.springframework.stereotype.Repository;

@Repository
public class SessionDatabaseRepository extends AbstractDatabaseRepository<Session> implements SessionRepository {

    public void removeAllBut(SSOToken ssoToken) {
        Query query = entityManager.createQuery("DELETE FROM SessionEntity s WHERE NOT s.ssoToken = :ssoToken");
        query.setParameter("ssoToken", ssoToken.getToken());
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Session findBySSOToken(SSOToken ssoToken) {
        return findBySSOToken(ssoToken.getToken());
    }

    public Session findBySSOToken(String ssoToken) {
        Query query = entityManager.createQuery(
                "SELECT s FROM SessionEntity s WHERE s.ssoToken = :ssoToken").setParameter(
                "ssoToken", ssoToken);
        List<Session> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }

        throw new IllegalStateException("Multiple sessions found for token: '" + ssoToken + "'");
    }

    @Override
    public Collection<Session> findAllActive() {
        return findAll();
    }

    @Override
    protected Class<? extends Session> getEntityType() {
        return SessionEntity.class;
    }
}
