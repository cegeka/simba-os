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
package org.simbasecurity.client.configuration;

import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.util.StringUtil;

import java.net.*;

public class SimbaConfiguration {
    private SimbaConfiguration() {
        // utility class should not be instantiated
    }

    public static String getSimbaLoginURL(String filteredBySecurityFilterRequestURL, String username, String password) {
        return filteredBySecurityFilterRequestURL + "?SimbaAction=SimbaLoginAction&username=" + username + "&password="
               + password;
    }

    public static String getSimbaLogoutURL(String filteredBySecurityFilterRequestURL) {
        return filteredBySecurityFilterRequestURL + "?SimbaAction=SimbaLogoutAction";
    }

    public static String getSimbaChangePasswordURL(String filteredBySecurityFilterRequestURL) {
        return filteredBySecurityFilterRequestURL + "?SimbaAction=SimbaShowChangePasswordAction";
    }

    public static String getSimbaAuthorizationURL() {
        return normalize(getSimbaURL() + "/thrift/authorizationService");
    }

    public static String getSimbaAuthenticationURL() {
        return normalize(getSimbaURL() + "/thrift/authenticationFilterService");
    }

    public static String getCacheServiceURL() {
        return normalize(getSimbaURL() + "/thrift/cacheService");
    }

    public static String getConfigurationServiceURL() {
        return normalize(getSimbaURL() + "/thrift/configurationService");
    }

    public static String getSessionServiceURL() {
        return normalize(getSimbaURL() + "/thrift/sessionService");
    }

    public static String getUserServiceURL() {
        return normalize(getSimbaURL() + "/thrift/userService");
    }

    public static String getGroupServiceURL() {
        return normalize(getSimbaURL() + "/thrift/groupService");
    }

    public static String getConditionServiceURL() {
        return normalize(getSimbaURL() + "/thrift/conditionService");
    }

    public static String getPolicyServiceURL() {
        return normalize(getSimbaURL() + "/thrift/policyService");
    }

    private static String getSimbaURL() {
        String url = SystemConfiguration.getSimbaServiceURL();
        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("Simba URL has not been set. Check system property [simba.url]");
        }

        url = resolveLocalhostToHostname(url);

        return url;
    }

    private static String resolveLocalhostToHostname(String urlToResolve) {
        String url = urlToResolve;
        if (url.contains("{localhost}")) {
            try {
                url = url.replaceAll("\\{localhost\\}", InetAddress.getLocalHost().getCanonicalHostName());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return url;
    }

    private static String normalize(String url) {
        try {
            URI uri = new URI(url);
            return uri.normalize().toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }
}