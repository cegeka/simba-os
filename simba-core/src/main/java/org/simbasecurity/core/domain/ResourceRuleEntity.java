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
package org.simbasecurity.core.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import javax.persistence.*;

import static java.lang.Boolean.FALSE;

@Entity
@SqlResultSetMapping(name = "cacheResourceRules", entities = { @EntityResult(entityClass = ResourceRuleEntity.class) }, columns = {
    @ColumnResult(name = "username"), @ColumnResult(name = "resourcename") })
@DiscriminatorValue("RESOURCE")
public class ResourceRuleEntity extends RuleEntity implements ResourceRule {

    private static final long serialVersionUID = -8032356730363671526L;

    @Column(name = "CREATE_ALLOWED")
    private Boolean createAllowed = FALSE;

    @Column(name = "DELETE_ALLOWED")
    private Boolean deleteAllowed = FALSE;

    @Column(name = "READ_ALLOWED")
    private Boolean readAllowed = FALSE;

    @Column(name = "WRITE_ALLOWED")
    private Boolean writeAllowed = FALSE;

    public ResourceRuleEntity() {
    }

    public ResourceRuleEntity(String name) {
        super(name);
    }

    @Override
    public boolean isAllowed(ResourceOperationType operation) {
        switch (operation) {
        case READ:
            return isReadAllowed();
        case WRITE:
            return isWriteAllowed();
        case CREATE:
            return isCreateAllowed();
        case DELETE:
            return isDeleteAllowed();
        case UNKNOWN:
            return false;
        default:
            throw new IllegalArgumentException("The specified operation could not be resolved");
        }
    }

    @Override
    public boolean isCreateAllowed() {
        return createAllowed;
    }

    @Override
    public void setCreateAllowed(boolean allowed) {
        this.createAllowed = allowed;
    }

    @Override
    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    @Override
    public void setDeleteAllowed(boolean allowed) {
        this.deleteAllowed = allowed;
    }

    @Override
    public boolean isReadAllowed() {
        return readAllowed;
    }

    @Override
    public void setReadAllowed(boolean allowed) {
        this.readAllowed = allowed;
    }

    @Override
    public boolean isWriteAllowed() {
        return writeAllowed;
    }

    @Override
    public void setWriteAllowed(boolean allowed) {
        this.writeAllowed = allowed;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
