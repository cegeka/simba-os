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
package org.simbasecurity.core.task;

import org.simbasecurity.core.audit.provider.DatabaseDigestAuditLogProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VerifyAuditLogIntegrityTask implements QuartzTask {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<List<DatabaseDigestAuditLogProvider>> databaseDigestAuditLogProviders;

    @Autowired
    public VerifyAuditLogIntegrityTask(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<List<DatabaseDigestAuditLogProvider>> databaseDigestAuditLogProviders) {
        this.databaseDigestAuditLogProviders = databaseDigestAuditLogProviders;
    }

    @Override
    public void execute() {
        databaseDigestAuditLogProviders.ifPresent(l -> {
            l.forEach(DatabaseDigestAuditLogProvider::verifyDigest);
        });
    }
}
