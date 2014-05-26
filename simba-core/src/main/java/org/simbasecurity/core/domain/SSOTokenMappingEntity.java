package org.simbasecurity.core.domain;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.config.ConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.locator.GlobalContext;

@Entity
@Table(name = "SIMBA_SSO_TOKEN_MAPPING")
public class SSOTokenMappingEntity extends AbstractEntity implements SSOTokenMapping {

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_SSO_TOKEN_MAPPING")
    protected long id = 0;

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
    public long getId() {
        return id;
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
        Integer maxElapsedTime = getConfigurationService().getValue(ConfigurationParameter.MAX_LOGIN_ELAPSED_TIME);
        return TimeUnit.MILLISECONDS.convert(maxElapsedTime, ConfigurationParameter.MAX_LOGIN_ELAPSED_TIME.getTimeUnit());
    }

    private ConfigurationService getConfigurationService() {
        return GlobalContext.locate(ConfigurationService.class);
    }
}
