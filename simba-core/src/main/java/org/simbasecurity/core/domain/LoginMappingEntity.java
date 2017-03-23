/*
 * Copyright 2011 Simba Open Source
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
 */
package org.simbasecurity.core.domain;


import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.config.ConfigurationService;
import org.simbasecurity.core.locator.GlobalContext;


@Entity
@Table(name = "SIMBA_LOGIN_MAPPING")
public class LoginMappingEntity extends AbstractEntity implements LoginMapping {

	private static final long serialVersionUID = -1883973037440862067L;

    @Id
    @GeneratedValue(generator = "simbaSequence", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "simbaSequence", sequenceName = "SEQ_SIMBA_LOGIN_MAPPING")
    protected long id = 0;
	
	private String token;
	private String targetURL;
	private long creationTime;
	
	public LoginMappingEntity() {
	}

	public LoginMappingEntity(String targetURL) {
		this.targetURL = targetURL;

		this.token = UUID.randomUUID().toString();
		this.creationTime = System.currentTimeMillis();
	}
	
    @Override
    public long getId() {
        return id;
    }	
	
	public String getToken() {
		return token;
	}

	public String getTargetURL() {
		return targetURL;
	}

	public long getCreationTime() {
		return creationTime;
	}

	@Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", id);
        builder.append("Token", token);
        builder.append("targetURL", targetURL);
        builder.append("TimeStamp", creationTime);
		return builder.toString();
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
