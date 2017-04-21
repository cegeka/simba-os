package org.simbasecurity.core.domain;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.locator.GlobalContext;

import javax.persistence.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "SIMBA_SSO_TOKEN_MAPPING")
public class SSOTokenMappingEntity implements SSOTokenMapping {

    @Id
    private String token;

    private String ssoToken;
    private long creationTime;

    public SSOTokenMappingEntity() {
    }

    public SSOTokenMappingEntity(SSOToken ssoToken) {
        this.token = UUID.randomUUID().toString();
        this.ssoToken = ssoToken.getToken();
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public SSOToken getSSOToken() {
        return new SSOToken(ssoToken);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean isExpired() {
        return creationTime + getMaxLoginElapsedTime() < System.currentTimeMillis();
    }

    private long getMaxLoginElapsedTime() {
        Integer maxElapsedTime = getConfigurationService().getValue(SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME);
        return TimeUnit.MILLISECONDS.convert(maxElapsedTime, SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME.getTimeUnit());
    }

    private ConfigurationService getConfigurationService() {
        return GlobalContext.locate(ConfigurationService.class);
    }
}
