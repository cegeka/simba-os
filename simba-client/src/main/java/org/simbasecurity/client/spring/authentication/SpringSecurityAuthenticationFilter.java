package org.simbasecurity.client.spring.authentication;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.simbasecurity.api.service.thrift.*;
import org.simbasecurity.client.configuration.SimbaConfiguration;
import org.simbasecurity.client.filter.action.FilterActionFactory;
import org.simbasecurity.client.principal.SimbaPrincipal;
import org.simbasecurity.common.config.SpringBootConfigurationProperties;
import org.simbasecurity.common.filter.action.MakeCookieAction;
import org.simbasecurity.common.request.RequestUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.simbasecurity.common.config.SystemConfiguration.*;
import static org.springframework.util.Assert.notNull;

/**
 * To use this filter in your spring boot application, you will need to configure this class as a bean:
 * <pre>
 * {@code @Bean}
 *  public SpringSecurityAuthenticationFilter filter() {
 *      return new SpringSecurityAuthenticationFilter();
 *  }
 * </pre>
 * <p>
 * To avoid Spring Boot registering the filter with your web container also define a FilterRegistrationBean.
 *
 * Then this filter can be used to configure your web security:
 * <pre>
 * {@code @Configuration}
 * {@code @EnableWebSecurity}
 * {@code @EnableConfigurationProperties(SpringBootConfigurationProperties.class)}
 * public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 *
 *     private final SpringSecurityAuthenticationFilter springBootSecurityAuthenticationFilter;
 *
 *    {@code @Autowired}
 *     public WebSecurityConfig(SpringSecurityAuthenticationFilter springBootSecurityAuthenticationFilter) {
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
 * <p>
 * The settings are passed using the autowired {@link SpringBootConfigurationProperties},
 * which can be configured in your application.properties file using the simba.properties.* prefix.
 */
public class SpringSecurityAuthenticationFilter implements Filter {

    private String simbaWebURL;
    private String simbaEidSuccessUrl;
    private String authenticationChainName;

    public SpringSecurityAuthenticationFilter(Map<String, String> configurationParameters) {
        simbaWebURL = configurationParameters.getOrDefault(SYS_PROP_SIMBA_WEB_URL, getSimbaWebURL());
        authenticationChainName = configurationParameters.getOrDefault(SYS_PROP_SIMBA_AUTHENTICATION_CHAIN_NAME, getAuthenticationChainName(emptyMap()));
        simbaEidSuccessUrl = configurationParameters.getOrDefault(SIMBA_EID_SUCCESS_URL, getSimbaEidSuccessUrl(null));
        MakeCookieAction.setSecureCookiesEnabled(getSecureCookiesEnabled(null));

        notNull(getSimbaServiceURL(), "Simba URL has not been set.");
        notNull(simbaWebURL, "Simba web URL has not been set.");
        notNull(authenticationChainName, "Simba authentication chain name has not been set.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        RequestData requestData = RequestUtil.createRequestData(servletRequest, simbaWebURL, simbaEidSuccessUrl);

        FilterActionFactory actionFactory = new FilterActionFactory(servletRequest, servletResponse, chain);

        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthenticationURL());
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);

            AuthenticationFilterService.Client authenticationClient = new AuthenticationFilterService.Client(tProtocol);
            ActionDescriptor actionDescriptor = authenticationClient.processRequest(requestData, authenticationChainName);

            if (actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)) {
                setSpringSecurityContext(servletRequest, actionDescriptor.getPrincipal());
            }

            actionFactory.execute(actionDescriptor);

            if (!actionDescriptor.getActionTypes().contains(ActionType.DO_FILTER_AND_SET_PRINCIPAL)
                    && !actionDescriptor.getActionTypes().contains(ActionType.REDIRECT)) {
                servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            servletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        } finally {
            if (tHttpClient != null) {
                tHttpClient.close();
            }
        }
    }

    protected void setSpringSecurityContext(HttpServletRequest servletRequest, String principal) {
        SecurityContext context = SecurityContextHolder.getContext();
        SimbaPrincipal simbaPrincipal = new SimbaPrincipal(principal);
        context.setAuthentication(new PreAuthenticatedAuthenticationToken(simbaPrincipal, null, getRoles(simbaPrincipal)));
        servletRequest.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    private List<SimpleGrantedAuthority> getRoles(Principal principal) {
        try {
            THttpClient tHttpClient = new THttpClient(SimbaConfiguration.getSimbaAuthorizationURL());
            TProtocol tProtocol = new TJSONProtocol(tHttpClient);
            AuthorizationService.Client client = new AuthorizationService.Client(tProtocol);
            List<String> roles = client.getRoles(principal.getName());
            return roles.stream()
                    .map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(toList());
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
