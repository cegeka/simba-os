/*
 * Copyright 2013 Simba Open Source
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
package org.simbasecurity.core.util;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

public class ExtendedManagedClassesPostProcessor implements PersistenceUnitPostProcessor {

    private String[] managedClassNames;

    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        if (managedClassNames == null || managedClassNames.length == 0) return;

        for (String managedClassName : getManagedClassNames()) {
            pui.addManagedClassName(managedClassName);
        }
    }

    public void setManagedClassNames(String[] managedClassNames) {
        this.managedClassNames = managedClassNames;
    }

    public String[] getManagedClassNames() {
        return managedClassNames;
    }
}