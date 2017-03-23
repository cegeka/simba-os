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
package org.simbasecurity.core.config.store;

import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationStore;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class DatabaseConfigurationStore implements ConfigurationStore {

    static final String PARAMETER_KEY = "PARAMETER_KEY";
    static final String PARAMETER_VALUE = "PARAMETER_VALUE";

    private static final String SQL_GET = "SELECT " + PARAMETER_VALUE + " FROM SIMBA_PARAMETER WHERE " + PARAMETER_KEY + " = ?";
    private static final String SQL_SET = "UPDATE SIMBA_PARAMETER SET " + PARAMETER_VALUE + " = ? WHERE " + PARAMETER_KEY + " = ?";
    private static final String SQL_DELETE = "DELETE FROM SIMBA_PARAMETER WHERE " + PARAMETER_KEY + " = ?";
    private static final String SQL_INSERT = "INSERT INTO SIMBA_PARAMETER (" + PARAMETER_KEY + ", " + PARAMETER_VALUE + ") VALUES (?, ?)";

    @PersistenceContext
    public EntityManager entityManager;

    public String getValue(ConfigurationParameter parameter) {
        Query query = entityManager.createNativeQuery(SQL_GET);
        query.setParameter(1, String.valueOf(parameter));
        try {
            return (String) query.getSingleResult();
        } catch(Exception ignore) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getValueList(ConfigurationParameter parameter) {
        Query query = entityManager.createNativeQuery(SQL_GET);
        query.setParameter(1, String.valueOf(parameter));
        return query.getResultList();
    }

    public String setValue(ConfigurationParameter parameter, String value) {
        String oldValue = getValue(parameter);

        Query query = entityManager.createNativeQuery(SQL_SET);
        query.setParameter(1, value);
        query.setParameter(2, String.valueOf(parameter));
        query.executeUpdate();

        return oldValue;
    }

    public List<String> setValueList(ConfigurationParameter parameter, List<String> valueList) {
        List<String> oldValue = getValueList(parameter);

        Query query = entityManager.createNativeQuery(SQL_DELETE);
        query.setParameter(1, String.valueOf(parameter));
        query.executeUpdate();

        for (String value : valueList) {
            query = entityManager.createNativeQuery(SQL_INSERT);
            query.setParameter(1, String.valueOf(parameter));
            query.setParameter(2, value);
            query.executeUpdate();
        }
        return oldValue;
    }
}
