/*
 * Copyright 2013-2017 Simba Open Source
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
 *
 */
package org.simbasecurity.core.domain.repository;

import org.simbasecurity.core.domain.Identifiable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;

public abstract class AbstractDatabaseRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public final Collection<T> findAll() {
        Query query = entityManager.createQuery("SELECT e FROM " + getEntityType().getName() + " e");
        return query.getResultList();
    }

    public void remove(T entity) {
        if (entity == null) return;

        entityManager.remove(entity);
    }

    public <S extends T> S persist(S entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T lookUp(Identifiable detachedEntity) {
        return entityManager.find(getEntityType(), detachedEntity.getId());
    }

    public T lookUp(long id) {
       return entityManager.find(getEntityType(), id);
    }

    @SuppressWarnings("unchecked")
    public final Collection<T> findAllByIds(Collection<Long> ids) {
        return entityManager.createQuery("SELECT e FROM " + getEntityType().getName() + " e where e.id in (:ids)")
                .setParameter("ids", ids)
                .getResultList();
    }

    protected abstract Class<? extends T> getEntityType();

    public void flush() {
        entityManager.flush();
    }
}