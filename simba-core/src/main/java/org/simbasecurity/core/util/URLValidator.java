package org.simbasecurity.core.util;

import static org.simbasecurity.core.config.ConfigurationParameter.*;

import org.apache.thrift.TException;
import org.simbasecurity.api.service.thrift.AuthorizationService;
import org.simbasecurity.common.config.SystemConfiguration;
import org.simbasecurity.common.util.StringUtil;
import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.simbasecurity.core.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLValidator {

    @Autowired private AuthorizationService.Iface authorizationService;
    @Autowired private Audit audit;
    @Autowired private AuditLogEventFactory auditLogFactory;
    @Autowired private ConfigurationService configurationService;

    public String getValidatedURL(String url, String userName) {
        try {
            if (this.authorizationService.isURLRuleAllowed(userName, url, "POST").isAllowed()) {
                return url;
            }
        } catch (TException ignore) {
        }

        logFailure(url, userName);
        return redirectToAccessDenied();
    }

    private void logFailure(String url, String userName) {
        audit.log(auditLogFactory.createEventForFailureInForm(userName, "Trying to access " + url));
    }

    private String redirectToAccessDenied() {
        String simbaURL = SystemConfiguration.getSimbaWebURL();
        if (StringUtil.isEmpty(simbaURL)) {
            simbaURL = SystemConfiguration.getSimbaServiceURL();
        }
        return simbaURL + configurationService.getValue(ACCESS_DENIED_URL);
    }
}
