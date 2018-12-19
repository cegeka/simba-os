package org.simbasecurity.client.rest;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.client.data.Tuple;
import org.simbasecurity.client.filter.action.FilterActionFactory;
import org.simbasecurity.client.interceptor.SimbaWSAuthenticationException;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.filter.action.MakeCookieAction;
import org.simbasecurity.common.request.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.list;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

public class BasicAuthenticationFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationFilter.class);

    private String simbaWebURL;
    private String simbaEidSuccessUrl;
    private String authenticationChainName;

    public BasicAuthenticationFilter() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.simbaWebURL = SystemConfiguration.getSimbaWebURL(filterConfig);
        this.authenticationChainName = SystemConfiguration.getAuthenticationChainName(filterConfig);
        this.simbaEidSuccessUrl = SystemConfiguration.getSimbaEidSuccessUrl(filterConfig);
        MakeCookieAction.setSecureCookiesEnabled(SystemConfiguration.getSecureCookiesEnabled(filterConfig));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            authorizeWithSimba(chain, (HttpServletRequest) request, (HttpServletResponse) response);
            ((HttpServletResponse) response).setStatus(OK.getStatusCode());
        } catch (SimbaWSAuthenticationException exception) {
            ((HttpServletResponse) response).setStatus(UNAUTHORIZED.getStatusCode());
            response.setContentType(APPLICATION_JSON);
            response.getWriter().write(format("{\"errorMessage\":\"%s\"}", exception.getMessage()));
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            ((HttpServletResponse) response).setStatus(INTERNAL_SERVER_ERROR.getStatusCode());
            response.getWriter().write(format("{\"errorMessage\":\"%s\"}", exception.getMessage()));
            response.setContentType(APPLICATION_JSON);
        }
    }

    private void authorizeWithSimba(FilterChain chain, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        Map<String, String> parameterMap = createParameterMap(servletRequest);
        Map<String, String> headers = createHeaderMap(servletRequest);
        FilterActionFactory actionFactory = new FilterActionFactory(servletRequest, servletResponse, chain);

        UserNamePassword userNamePassword = UserNamePassword.fromBasicAuthenticationHeader(headers.get("Authorization"));
        parameterMap.put("username", userNamePassword.getUserName());
        parameterMap.put("password", userNamePassword.getPassword());

        RequestData requestData = new RequestData(
                parameterMap,
                headers,
                servletRequest.getRequestURI(),
                this.simbaWebURL,
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                servletRequest.getMethod(),
                RequestUtil.HOST_SERVER_NAME,
                null,
                simbaEidSuccessUrl);

        sendRequestAndContinueChain(requestData, actionFactory);
    }

    private void sendRequestAndContinueChain(RequestData requestData, FilterActionFactory actionFactory) {
        try (THttpClient tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthenticationURL())) {
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);
            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);
            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, this.authenticationChainName);

            setPrincipalAndContinueChain(actionFactory, actionDescriptor);
            if (!actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL) && !actionDescriptor.getActionTypes().contains(ActionType.REDIRECT)) {
                throw new SimbaWSAuthenticationException("Authentication Failed");
            }
        } catch (Exception exception) {
            throw new SimbaWSAuthenticationException("Authentication Failed");
        }
    }

    private void setPrincipalAndContinueChain(FilterActionFactory actionFactory, ActionDescriptor actionDescriptor) throws Exception {
        actionFactory.execute(actionDescriptor);
    }

    private Map<String, String> createHeaderMap(HttpServletRequest servletRequest) {
        return list(servletRequest.getHeaderNames()).stream()
                .map(headerName -> new Tuple<>(headerName, servletRequest.getHeader(headerName)))
                .collect(Collectors.toMap(Tuple::getFirstObject, Tuple::getSecondObject));
    }

    private Map<String, String> createParameterMap(HttpServletRequest servletRequest) {
        return servletRequest.getParameterMap().entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()[0]))
                .collect(Collectors.toMap(Tuple::getFirstObject, Tuple::getSecondObject));
    }

    @Override
    public void destroy() {

    }
}
