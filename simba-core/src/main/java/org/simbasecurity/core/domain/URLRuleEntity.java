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
@SqlResultSetMapping(name = "cacheURLRules",
    entities = {
        @EntityResult(entityClass = URLRuleEntity.class)
    },
    columns = {
        @ColumnResult(name = "username")
    })
@DiscriminatorValue("URL")
public class URLRuleEntity extends RuleEntity implements URLRule {

    private static final long serialVersionUID = 139522090442852901L;

    @Column(name = "GET_ALLOWED")
    private Boolean getAllowed = FALSE;

    @Column(name = "POST_ALLOWED")
    private Boolean postAllowed = FALSE;

    @Column(name = "PUT_ALLOWED")
    private Boolean putAllowed = FALSE;

    @Column(name = "DELETE_ALLOWED")
    private Boolean deleteAllowed = FALSE;

    public URLRuleEntity() {
    }

    public URLRuleEntity(String name) {
        super(name);
    }

    @Override
    public boolean isGetAllowed() {
        return getAllowed;
    }

    @Override
    public boolean isPostAllowed() {
        return postAllowed;
    }

    @Override
    public boolean isPutAllowed() {
        return putAllowed;
    }

    @Override
    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    @Override
    public void setGetAllowed(boolean allowed) {
        this.getAllowed = allowed;
    }

    @Override
    public void setPostAllowed(boolean allowed) {
        this.postAllowed = allowed;
    }

    @Override
    public void setPutAllowed(boolean putAllowed) {
        this.putAllowed = putAllowed;
    }

    @Override
    public void setDeleteAllowed(boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public boolean isAllowed(URLOperationType operation) {
        switch (operation) {
        case GET:
            return isGetAllowed();
        case POST:
            return isPostAllowed();
        case HEAD:
            return isGetAllowed();
        case PUT:
            return isPutAllowed();
        case DELETE:
            return isDeleteAllowed();
        case OPTIONS:
            return false;
        case TRACE:
            return false;
        case UNKNOWN:
            return false;
        default:
            throw new IllegalArgumentException("The specified operation could not be resolved");
        }
    }
}
