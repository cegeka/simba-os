package org.simbasecurity.client.spring.authentication;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.ActionDescriptor;
import org.simbasecurity.api.service.thrift.ActionType;
import org.simbasecurity.api.service.thrift.AuthenticationFilterService;
import org.simbasecurity.api.service.thrift.RequestData;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.common.config.SpringBootConfigurationProperties;
import org.simbasecurity.client.principal.SimbaPrincipal;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.request.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * To use this filter in your spring boot application, you will need to configure this class as a bean:
 * <pre>
 * {@code @Bean}
 *  public SpringBootSecurityAuthenticationFilter filter() {
 *      return new SpringBootSecurityAuthenticationFilter();
 *  }
 * </pre>
 *
 * Then this filter can be used to configure your web security:
 * <pre>
 *{@code @Configuration}
 *{@code @EnableWebSecurity}
 *{@code @EnableConfigurationProperties(SpringBootConfigurationProperties.class)}
 * public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 *
 *     private final SpringBootSecurityAuthenticationFilter springBootSecurityAuthenticationFilter;
 *
 *    {@code @Autowired}
 *     public WebSecurityConfig(SpringBootSecurityAuthenticationFilter springBootSecurityAuthenticationFilter) {
 *         this.springBootSecurityAuthenticationFilter = springBootSecurityAuthenticationFilter;
 *     }
 *
 *    {@code @Override}
 *     protected void configure(HttpSecurity http) throws Exception {
 *         http.addFilterBefore(springBootSecurityAuthenticationFilter, BasicAuthenticationFilter.class)
 *             .authorizeRequests()
 *             .antMatchers("/query", "/downloadquery").authenticated()
 *             .and().csrf().disable();
 *         super.configure(http);
 *     }
 * }
 * </pre>
 *
 * The settings are passed using the autowired {@link SpringBootConfigurationProperties},
 * which can be configured in your application.properties file using the simba.properties.* prefix.
 */
public final class SpringBootSecurityAuthenticationFilter implements Filter, Ordered {

    @Autowired
    private SpringBootConfigurationProperties simbaConfigurationProperties;

    private String simbaWebURL;
    private String simbaURL;
    private String authenticationChainName;

    /**
     * @param filterConfig the FilterConfig does noet contain any settings for this filter. The settings are passed using the autowired {@link SpringBootConfigurationProperties},
     *                     which can be configured in your application.properties file using the simba.properties.* prefix.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        simbaURL = SystemConfiguration.getSimbaServiceURL(simbaConfigurationProperties);
        if (simbaURL == null) {
            throw new ServletException("Simba URL has not been set. Check org.simbasecurity.client.filter params or system property [" + SystemConfiguration.SYS_PROP_SIMBA_INTERNAL_SERVICE_URL + "]");
        }

        simbaWebURL = SystemConfiguration.getSimbaWebURL(simbaConfigurationProperties);
        if (simbaWebURL == null) {
            throw new ServletException("Simba web URL has not been set. Check org.simbasecurity.client.filter params or system property [" + SystemConfiguration.SYS_PROP_SIMBA_WEB_URL + "]");
        }

        authenticationChainName = SystemConfiguration.getAuthenticationChainName(simbaConfigurationProperties);
        if (authenticationChainName == null) {
            throw new ServletException("Simba authentication chain name has not been set. Check org.simbasecurity.client.filter params or system property ["
                    + SystemConfiguration.SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME
                    + "]");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        RequestData requestData = RequestUtil.createRequestData(servletRequest, simbaWebURL, null);

        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthenticationURL());
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);

            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, authenticationChainName);
            if (!actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)) {
                servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new PreAuthenticatedAuthenticationToken(new SimbaPrincipal(actionDescriptor.getPrincipal()), null, null));
            servletRequest.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        } catch (Exception e) {
            e.printStackTrace();
            servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        } finally {
            if (tHttpClient != null) {
                tHttpClient.close();
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
