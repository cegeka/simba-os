package org.simbasecurity.core.service.http;

import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletRequestTestBuilder {

    private String scheme;
    private String serverName;
    private int serverPort;
    private String contextPath;

    public static HttpServletRequestTestBuilder httpServletRequest() {
        return new HttpServletRequestTestBuilder();
    }

    public HttpServletRequest build() {
        HttpServletRequest mock = mock(HttpServletRequest.class);
        when(mock.getContextPath()).thenReturn(contextPath);
        when(mock.getServerName()).thenReturn(serverName);
        when(mock.getServerPort()).thenReturn(serverPort);
        when(mock.getScheme()).thenReturn(scheme);
        return mock;
    }

    public HttpServletRequestTestBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public HttpServletRequestTestBuilder serverName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public HttpServletRequestTestBuilder serverPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public HttpServletRequestTestBuilder contextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }
}
