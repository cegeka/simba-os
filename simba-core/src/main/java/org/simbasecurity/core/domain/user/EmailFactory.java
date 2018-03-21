package org.simbasecurity.core.domain.user;

import org.apache.commons.lang.StringUtils;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.exception.SimbaMessageKey;
import org.simbasecurity.core.service.config.CoreConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailFactory {

    private static final String EMAIL_PATTERN = "^\\w+[\\w_\\.\\-]*@[\\w_\\-]+\\.[\\w_\\.\\-]+$";

    private CoreConfigurationService configurationService;

    @Autowired
    public EmailFactory(CoreConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public EmailAddress email(String email) {
        if(!isEmailRequired() && isBlankOrEmpty(email)){
            return emptyEmail();
        }
        if(isBlankOrEmpty(email)){
            throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_REQUIRED);
        }
        if(!email.matches(EMAIL_PATTERN)) {
            throw new SimbaException(SimbaMessageKey.EMAIL_ADDRESS_INVALID, String.format("%s is not a valid email address", email));
        }
        return new EmailAddress(email);
    }

    public EmailAddress emptyEmail() {
        return new EmailAddress(null);
    }

    public EmailAddress orEmpty(EmailAddress email) {
        return email != null ? email : emptyEmail();
    }

    private boolean isBlankOrEmpty(String email) {
        return StringUtils.isEmpty(email) || StringUtils.isBlank(email);
    }

    private boolean isEmailRequired() {
        return configurationService.getValue(SimbaConfigurationParameter.EMAIL_ADDRESS_REQUIRED);
    }


}
