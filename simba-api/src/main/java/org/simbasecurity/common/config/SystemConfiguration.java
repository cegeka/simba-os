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
package org.simbasecurity.common.config;

import org.simbasecurity.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static java.lang.System.getProperty;

/**
 * This class provides utility methods to access several system configuration
 * properties.
 * <p/>
 * The primary setting for these properties is always done through System
 * properties. If there is no System property provided, then the value can be
 * retrieve using another (default) configuration mechanism (Servlet Filter
 * configuration, URL reconstruction, ...).
 * <p/>
 * If an URL contains the String "{localhost}", then this part of the URL will
 * be replaced by the full host name of the server running Simba. See
 * {@link java.net.InetAddress#getLocalHost()} and
 * {@link java.net.InetAddress#getCanonicalHostName()} for more information.
 */
public final class SystemConfiguration {

    /**
     * System property for the Simba Service URL. For internal usages (like
     * thrift, ...)
     */
    public static final String SYS_PROP_SIMBA_INTERNAL_SERVICE_URL = "simba.url";

    /**
     * System property for the Simba Web URL. For external usage (like login
     * parameter)
     */
    public static final String SYS_PROP_SIMBA_WEB_URL = "simba.web.url";

    public static final String SYS_PROP_SIMBA_SECURE_COOKIES_ENABLED = "simba.secure.cookies.enabled";

    public static final String SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME = "simba.authentication.chain.name";
    public static final String SYS_PROP_SIMBA_MANAGER_AUTHORIZATION_CHAIN_NAME = "simba.manager.authorization.chain.name";
    public static final String EXCLUDED_URL = "excludedUrl";
    public static final String SIMBA_EID_SUCCESS_URL = "simba.eid.success.url";

    private static final Logger LOG = LoggerFactory.getLogger(SystemConfiguration.class);
    private static final Properties simbaProperties = new Properties();
    private static boolean propertiesLoaded = false;

    private SystemConfiguration() {
    }

    /**
     * Get the configuration value for the Simba Service URL, using the
     * specified {@link FilterConfig} as default.
     *
     * @param config the FilterConfig from which to get default configuration in
     *               the init parameters.
     * @return the configuration value for the Simba Service URL. When there is
     * no configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_INTERNAL_SERVICE_URL
     */
    public static String getSimbaServiceURL(final FilterConfig config) {
        return getSimbaServiceURL(getDefaultsMap(config));
    }

    /**
     * Get the configuration value for the Simba Service URL, using the
     * specified {@link ServletContext} as default.
     *
     * @param servletContext the ServletContext from which to get default configuration in
     *                       the init parameters.
     * @return the configuration value for the Simba Service URL. When there is
     * no configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_INTERNAL_SERVICE_URL
     */
    public static String getSimbaServiceURL(final ServletContext servletContext) {
        return getSimbaServiceURL(getDefaultsMap(servletContext));
    }

    /**
     * Get the configuration value for the Simba Service URL, using the
     * specified {@link SpringBootConfigurationProperties} as default.
     *
     * @param properties the SpringBootConfigurationProperties from which to get default configuration in
     *                       the init parameters.
     * @return the configuration value for the Simba Service URL. When there is
     * no configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_INTERNAL_SERVICE_URL
     */
    public static String getSimbaServiceURL(final SpringBootConfigurationProperties properties) {
        return getSimbaServiceURL(getDefaultsMap(properties));
    }

    /**
     * Get the configuration value for the Simba Service URL without using any
     * defaults.
     *
     * @return the configuration value for the Simba Service URL. When there is
     * no configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_INTERNAL_SERVICE_URL
     */
    public static String getSimbaServiceURL() {
        return getSimbaServiceURL(Collections.emptyMap());
    }

    /**
     * Get the configuration value for the Simba Web URL, using the specified
     * {@link FilterConfig} as default. If this property is not configured, the
     * Simba Service URL is returned instead.
     *
     * @param config the FilterConfig from which to get default configuration in
     *               the init parameters.
     * @return the configuration value for the Simba Web URL. When there is no
     * configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_WEB_URL
     * @see #getSimbaServiceURL()
     */
    public static String getSimbaWebURL(final FilterConfig config) {
        return getSimbaWebURL(getDefaultsMap(config));
    }

    /**
     * Get the configuration value for the Simba Web URL, using the specified
     * {@link ServletContext} as default. If this property is not configured,
     * the Simba Service URL is returned instead.
     *
     * @param servletContext the ServletContext from which to get default configuration in
     *                       the init parameters.
     * @return the configuration value for the Simba Web URL. When there is no
     * configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_WEB_URL
     * @see #getSimbaServiceURL()
     */
    public static String getSimbaWebURL(final ServletContext servletContext) {
        return getSimbaWebURL(getDefaultsMap(servletContext));
    }

    /**
     * Get the configuration value for the Simba Web URL, using the specified
     * {@link SpringBootConfigurationProperties} as default. If this property is not configured,
     * the Simba Service URL is returned instead.
     *
     * @param properties the ServletContext from which to get default configuration in
     *                       the init parameters.
     * @return the configuration value for the Simba Web URL. When there is no
     * configuration, {@code null} is returned.
     * @see #SYS_PROP_SIMBA_WEB_URL
     * @see #getSimbaServiceURL()
     */
    public static String getSimbaWebURL(final SpringBootConfigurationProperties properties) {
        return getSimbaWebURL(getDefaultsMap(properties));
    }

    /**
     * Get the configuration value for the Simba Web URL without using any
     * defaults.
     *
     * @return the configuration value for the Simba Web URL
     * @see #SYS_PROP_SIMBA_WEB_URL
     * @see #getSimbaServiceURL()
     */
    public static String getSimbaWebURL() {
        return getSimbaWebURL(Collections.<String, String>emptyMap());
    }

    public static String getAuthenticationChainName(final FilterConfig filterConfig) {
        return getAuthenticationChainName(getDefaultsMap(filterConfig));
    }

    public static String getAuthenticationChainName(final ServletContext servletContext) {
        return getAuthenticationChainName(getDefaultsMap(servletContext));
    }

    public static String getAuthenticationChainName(final SpringBootConfigurationProperties properties) {
        return getAuthenticationChainName(getDefaultsMap(properties));
    }

    private static String getSimbaServiceURL(final Map<String, String> defaults) {
        final String url = resolveConfigurationParameter(SYS_PROP_SIMBA_INTERNAL_SERVICE_URL, defaults);

        if (StringUtil.isEmpty(url)) {
            return null;
        }

        return resolveLocalhostToHostname(url);
    }

    private static String getSimbaWebURL(final Map<String, String> defaults) {
        final String url = resolveConfigurationParameter(SYS_PROP_SIMBA_WEB_URL, defaults);
        if (StringUtil.isEmpty(url)) {
            return getSimbaServiceURL(defaults);
        } else {
            return resolveLocalhostToHostname(url);
        }
    }

    public static String getManagerAuthorizationChainName() {
        return resolveConfigurationParameter(SYS_PROP_SIMBA_MANAGER_AUTHORIZATION_CHAIN_NAME, new HashMap<>());
    }

    public static String getAuthenticationChainName(final Map<String, String> defaults) {
        return resolveConfigurationParameter(SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME, defaults);
    }

    private static String resolveConfigurationParameter(final String parameterName, final Map<String, String> defaults) {
        String value = getSimbaProperty(parameterName);
        if (StringUtil.isEmpty(value)) {
            value = getProperty(parameterName);
            if (StringUtil.isEmpty(value)) {
                value = defaults.get(parameterName);
                if (StringUtil.isEmpty(value)) {
                    LOG.warn("The parameter " + parameterName + " was not set. This could cause unexpected behaviour.");
                }
            }
        }
        return value;
    }

    private static String getSimbaProperty(String key) {
        if (!propertiesLoaded) {
            loadSimbaProperties();
        }
        return simbaProperties.getProperty(key);
    }

    private static void loadSimbaProperties() {
        try {
            String propertyFileLocation = System.getProperty("simba.properties.file");
            if (propertyFileLocation != null) {
                InputStream propertiesFile = new FileInputStream(propertyFileLocation);
                SystemConfiguration.simbaProperties.load(propertiesFile);
            } else {
                LOG.info("no simba.properties file configured, falling back to using System properties");
            }
            propertiesLoaded = true;
        } catch (Exception e) {
            LOG.warn("error loading simba.properties file in SystemConfiguration", e);
        }
    }

    private static String resolveLocalhostToHostname(final String urlToResolve) {
        String url = urlToResolve;
        if (url.contains("{localhost}")) {
            try {
                url = url.replaceAll("\\{localhost\\}", InetAddress.getLocalHost().getCanonicalHostName());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getDefaultsMap(final FilterConfig config) {
        if (config == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> defaults = getDefaultsMap(config.getServletContext());

        final Enumeration<String> names = config.getInitParameterNames();

        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            defaults.put(name, config.getInitParameter(name));
        }
        return defaults;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getDefaultsMap(final ServletContext context) {
        if (context == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> defaults = new HashMap<>();

        final Enumeration<String> names = context.getInitParameterNames();

        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            defaults.put(name, context.getInitParameter(name));
        }
        return defaults;
    }

    private static Map<String, String> getDefaultsMap(final SpringBootConfigurationProperties context) {
        if (context == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> defaults = new HashMap<>();

        for (Map.Entry<String, String> entry : context.getProperties().entrySet()) {
            defaults.put(entry.getKey(), entry.getValue());
        }
        return defaults;
    }

    public static boolean getSecureCookiesEnabled(FilterConfig filterConfig) {
        String secureCookiesEnabled = resolveConfigurationParameter(SYS_PROP_SIMBA_SECURE_COOKIES_ENABLED, getDefaultsMap(filterConfig));
        return Boolean.valueOf(secureCookiesEnabled);
    }

    public static String getExclusionContextPath(FilterConfig filterConfig) {
        return resolveConfigurationParameter(EXCLUDED_URL, getDefaultsMap(filterConfig));
    }

    public static String getSimbaEidSuccessUrl(FilterConfig filterConfig) {
        return resolveConfigurationParameter(SIMBA_EID_SUCCESS_URL, getDefaultsMap(filterConfig));
    }
}
