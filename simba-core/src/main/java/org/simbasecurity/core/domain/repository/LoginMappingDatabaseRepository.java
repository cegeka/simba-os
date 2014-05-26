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

import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.springframework.stereotype.Repository;

@Repository
public class LoginMappingDatabaseRepository extends AbstractDatabaseRepository<LoginMapping> implements LoginMappingRepository {

	@SuppressWarnings("unchecked")
	@Override
	public LoginMapping findByToken(String token) {
		Query query = entityManager.createQuery("SELECT lm FROM LoginMappingEntity lm WHERE lm.token = :token").setParameter("token", token);
		
		List<LoginMapping> resultList = query.getResultList();
		if (resultList.size() == 0) {
			return null; //can also be when token is expired/cleaned up
		} else if (resultList.size() == 1) {
			return resultList.get(0);
		}

		throw new IllegalStateException("Multiple mappings found for token: '" + token + "'");
	}

	@Override
	public void remove(String token) {
		Query query = entityManager.createQuery("DELETE FROM LoginMappingEntity lm WHERE lm.token = :token");
		query.setParameter("token", token);
		query.executeUpdate();
	}

	@Override
	protected Class<? extends LoginMapping> getEntityType() {
		return LoginMappingEntity.class;
	}
	
}