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