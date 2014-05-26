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
package org.simbasecurity.core.util;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.jasypt.digest.config.SimpleStringDigesterConfig;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;

public class PasswordEncryptorFactory extends BasePoolableObjectFactory {

    private final static String ALGORITHM = "SHA-1";

    @Override
    public Object makeObject() throws Exception {

        ConfigurablePasswordEncryptor configurablePasswordEncryptor = new ConfigurablePasswordEncryptor();
        configurablePasswordEncryptor.setAlgorithm(ALGORITHM);
        configurablePasswordEncryptor.setConfig(createMinimumSafeStringDigester(ALGORITHM));
        configurablePasswordEncryptor.setPlainDigest(false);
        return configurablePasswordEncryptor;
    }

    private SimpleStringDigesterConfig createMinimumSafeStringDigester(String algorithm) {

        SimpleStringDigesterConfig simpleStringDigesterConfig = new SimpleStringDigesterConfig();
        simpleStringDigesterConfig.setAlgorithm(algorithm);
        simpleStringDigesterConfig.setSaltSizeBytes(8);
        simpleStringDigesterConfig.setIterations(1000);
        simpleStringDigesterConfig.setSaltGenerator(new RandomSaltGenerator());
        return simpleStringDigesterConfig;
    }

}
