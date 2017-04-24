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
package org.simbasecurity.core.domain;

import java.io.Serializable;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Identifiable {

    private static final long serialVersionUID = -1650025108997404638L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);

    private transient boolean markedForRemoval = false;

    @Override
    public abstract long getId();

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AbstractEntity)) {
            return false;
        }

        return isPersisted() ? getId() == ((AbstractEntity) other).getId() : super.equals(other);
    }

    @Override
    public int hashCode() {
        return isPersisted() ? new HashCodeBuilder().append(getId()).toHashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return asString();
    }

    private String asString() {
        return this.getClass().getSimpleName() + '-' + getId();
    }

    private boolean isPersisted() {
        boolean isPersisted = getId() > 0;

        if (!isPersisted) {
            LOGGER.warn("Missing id for {}: using equals() or hashCode() before persisting an Entity is discouraged.",
                        asString());
        }

        return isPersisted;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }
}