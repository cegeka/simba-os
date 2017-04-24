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
package org.simbasecurity.core.service.manager.dto;

public final class ResourceRuleDTO extends RuleDTO {

    private boolean readAllowed;
    private boolean writeAllowed;
    private boolean createAllowed;
    private boolean deleteAllowed;

    public boolean isReadAllowed() {
        return readAllowed;
    }

    public void setReadAllowed(final boolean readAllowed) {
        this.readAllowed = readAllowed;
    }

    public boolean isWriteAllowed() {
        return writeAllowed;
    }

    public void setWriteAllowed(final boolean writeAllowed) {
        this.writeAllowed = writeAllowed;
    }

    public boolean isCreateAllowed() {
        return createAllowed;
    }

    public void setCreateAllowed(final boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(final boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }
}
