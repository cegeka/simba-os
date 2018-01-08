package org.simbasecurity.core.service.http;

import org.simbasecurity.common.config.SystemConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class SimbaWebUrlResolver {

    public String resolveSimbaWebURL(HttpServletRequest request) throws ServletException {
        String url = SystemConfiguration.getSimbaWebURL();

        if (url == null) {
            url = reconstructSimbaWebURL(request);
        }

        return url;
    }

    private String reconstructSimbaWebURL(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") + request.getContextPath();
    }
}
