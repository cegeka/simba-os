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

import java.util.List;
import javax.persistence.Query;

import org.simbasecurity.core.domain.SSOTokenMapping;
import org.simbasecurity.core.domain.SSOTokenMappingEntity;
import org.springframework.stereotype.Repository;

@Repository
public class SSOTokenMappingDatabaseRepository extends
		AbstractDatabaseRepository<SSOTokenMapping> implements
		SSOTokenMappingRepository {

    @SuppressWarnings("unchecked")
    @Override
    public SSOTokenMapping findByToken(String tokenKey) {
        Query query = entityManager
                      .createQuery(
                                  "SELECT tm FROM SSOTokenMappingEntity tm WHERE tm.token = :token")
                      .setParameter("token", tokenKey);
        List<SSOTokenMapping> resultList = query.getResultList();

        if (resultList.size() == 0) {
            return null;
        } else if (resultList.size() == 1) {
            return resultList.get(0);
        }

        throw new IllegalStateException("Multiple mappings found for tokenKey: '" + tokenKey + "'");
    }

    public void remove(String token) {
		Query query = entityManager
				.createQuery("DELETE FROM SSOTokenMappingEntity tm WHERE tm.token = :token");
		query.setParameter("token", token);
		query.executeUpdate();
	}

	@Override
	protected Class<? extends SSOTokenMapping> getEntityType() {
		return SSOTokenMappingEntity.class;
	}
}