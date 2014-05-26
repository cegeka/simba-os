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

import static org.simbasecurity.core.exception.SimbaMessageKey.*;

import java.util.ArrayList;
import java.util.Collection;

import org.simbasecurity.core.domain.Versionable;
import org.simbasecurity.core.exception.SimbaException;

public abstract class AbstractVersionedDatabaseRepository<T extends Versionable> extends AbstractDatabaseRepository<T> {

    public Collection<T> refreshWithOptimisticLocking(Collection<? extends Versionable> objects) {
        Collection<T> refreshedCollection = new ArrayList<T>(objects.size());
        for (Versionable object : objects) {
            refreshedCollection.add(refreshWithOptimisticLocking(object));
        }

        return refreshedCollection;
    }

    public T refreshWithOptimisticLocking(Versionable detachedEntity) {
        T refreshedEntity = lookUp(detachedEntity);
        checkOptimisticLocking(detachedEntity, refreshedEntity);
        return refreshedEntity;
    }

    private void checkOptimisticLocking(Versionable detachedEntity, Versionable attachedEntity) {
        if (detachedEntity == null) {
            String message = "Detached entity must not be null";
            throw new IllegalArgumentException(message);
        }

        if (attachedEntity == null) {
            throw new SimbaException(OPTIMISTIC_LOCK);
        }

        Integer detachedEntityVersion = detachedEntity.getVersion();
        Integer attachedEntityVersion = attachedEntity.getVersion();
        if (!detachedEntityVersion.equals(attachedEntityVersion)) {
            throw new SimbaException(OPTIMISTIC_LOCK);
        }
    }
}