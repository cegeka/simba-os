package org.simbasecurity.core.service.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultSecurityConfiguration;
import org.springframework.context.annotation.Configuration;

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

        throw new IllegalArgumentException("Failed to load " + RESOURCE_FILE + " as a classloader resource.");
    }

}
