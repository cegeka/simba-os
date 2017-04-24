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

package org.simbasecurity.core.jaas;

import java.net.URL;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaasConfigListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(JaasConfigListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        if (System.getProperty("java.security.auth.login.config") == null) {
            URL configURL = this.getClass().getClassLoader().getResource("login.conf");
            if (configURL != null) {
                String configFile = configURL.getFile();
                System.setProperty("java.security.auth.login.config", configFile);
                LOG.info("Configured JAAS to use config file at {}", configFile);
            }
        }
    }
}