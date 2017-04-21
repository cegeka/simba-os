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

import org.simbasecurity.core.domain.Identifiable;

import java.util.Collection;

public interface AbstractRepository<T> {

    Collection<T> findAll();

    void remove(T object);

    <S extends T> S persist(S object);

    T lookUp(Identifiable detachedEntity);

    T lookUp(long id);

    public Collection<T> findAllByIds(Collection<Long> ids);

    void flush();
}