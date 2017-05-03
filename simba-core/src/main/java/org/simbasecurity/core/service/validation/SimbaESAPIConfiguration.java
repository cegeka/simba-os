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

package org.simbasecurity.core.service.validation;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultSecurityConfiguration;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class SimbaESAPIConfiguration extends DefaultSecurityConfiguration {

    static {
        ESAPI.override(new SimbaESAPIConfiguration());
    }

    /**
     * See super class, we added fallback to classpath
     */
    public InputStream getResourceStream(String filename) throws IOException {
        if (filename == null) {
            return null;
        }

        try {
            File f = getResourceFile(filename);
            if (f != null && f.exists()) {
                return new FileInputStream(f);
            }

            InputStream in = loadResourceFromClasspath(filename);
            if (in != null) {
                return in;
            }
        } catch (Exception ignored) {
        }

        throw new FileNotFoundException();
    }

    private InputStream loadResourceFromClasspath(String fileName) throws IllegalArgumentException {
        InputStream in = null;

		ClassLoader[] loaders = new ClassLoader[] { Thread.currentThread().getContextClassLoader(), ClassLoader.getSystemClassLoader(), getClass().getClassLoader() };
		
        for (ClassLoader currentLoader : loaders) {
            if (currentLoader != null) {
                try {
                    // try root
                    in = currentLoader.getResourceAsStream(fileName);

                    // try .esapi folder. Look here first for backward compatibility.
                    if (in == null) {
                        in = currentLoader.getResourceAsStream(".esapi/" + fileName);
                    }

                    // try esapi folder (new directory)
                    if (in == null) {
                        in = currentLoader.getResourceAsStream("esapi/" + fileName);
                    }

                    // try resources folder
                    if (in == null) {
                        in = currentLoader.getResourceAsStream("resources/" + fileName);
                    }

                    // now load the properties
                    if (in != null) {
                        return in;
                    }
                } catch (Exception e) {
                    in = null;
                }
            }
        }

        throw new IllegalArgumentException("Failed to load " + DEFAULT_RESOURCE_FILE + " as a classloader resource.");
    }

}
